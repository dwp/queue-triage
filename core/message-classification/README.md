# Message Classification

Message Classification is really the core of the queue-triage application.

Once a messages has been Dead-Lettered by an Application, queue triage:
* De-queues the message
* Classifies the message using a set of Predicates
* Performs one or more Actions

A Message Classifier contains a `FailedMessagePredicate` and a `FailedMessageAction`.

#### Message Classifier with single Predicate and Action

```json
{
  "predicate": {
       "_type": "destinationEqualTo",
       "destination": "destination-name"
  },
  "action": {
    "_name": "AddLabelAction",
    "label": "SomeLabel"
  }
}
```

#### Message Classifier with Multiple Predicates with chained Actions

The following example demonstrates an more complex example with a predicate equivilent to
`WHERE destination == 'destination-name' AND ( message.contains('%foo%') OR property['ham'] == 'eggs') )` if matched, the following actions will be executed in order:
* `LabelMessageAction`
* `ResendFailedMessageAction`
```json
{
  "predicate": {
    "_type": "and",
    "predicates": [ {
        "_type": "destinationEqualTo",
        "destination": "destination-name"
      },
      {
        "_type": "or",
        "predicates": [ {
            "_type": "contentEqualTo",
            "content": "foo"
          },
          {
            "_type": "propertyEqualTo",
            "name": "ham",
            "value": "eggs"
          }
        ]  
      }
    ]
  },
  "action": {
    "_action": "chained",
    "actions": [
      {
        "_action": "label",
        "label": "foo"
      },
      {
        "_action": "resend",
        "destination": {
          "broker": "internal-broker",
          "destination": "some-queue"
        }
      }
    ]
  }
}
```

## Failed Message Predicates
A number of Failed Message Predicates are provided:
* AndPredicate - All of the given predicates must evaluate to `true`
* OrPredicate - Any of the given predicates evaluate to `true`
* BrokerEqualToPredicate - Name of the Broker equals a given value
* ContentEqualToPredicate - Content of the message equals a given value
* DestinationEqualToPredicate - Name of the Destination equals a given value
* PropertyEqualToPredicate - Value of a JMS property equals a given value
* PropertyExistsPredicate - Given JMS property exists
* PropertyMatchesPredicate - Value of a JMS property matches a given regular expression

### Custom Predicate Registration
Queue Triage allows you to easily write your own predicates by implementing the `FailedMessagePredicate` interface.

Once defined the new Predicate must be registered as a sub-type with the `ObjectMapper` (as demonstrated below):

```java
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.registerSubtypes(new NamedType(MyPredicate.class, "custom"));
```

An example can be seen in the [CustomPredicateRegistrationTest](src/test/java/uk/gov/dwp/queue/triage/core/classification/predicate/CustomPredicateRegistrationTest.java). 

