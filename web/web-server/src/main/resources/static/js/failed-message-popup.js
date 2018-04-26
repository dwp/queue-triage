$(function() {
    var config = {
        layout: {
            name: 'layout',
            padding: 4,
            panels: [
                { type: 'left', size: '50%', resizable: true, content: 'left' },
                { type: 'main', content: 'main' },
                { type: 'bottom', size: 250 }
            ]
        },
        statusHistory: {
            name: 'statusHistory',
            header: 'Status History',
            show: {
                header: true,
                columnHeaders: false
            },
            columns: [
                { field: 'recid', caption: 'recid', hidden: true },
                { field: 'status', caption: 'Status', size: '20%' },
                { field: 'effectiveDateTime', caption: 'Effective Date', size: '80%' }
            ],
            onRender: function(event) {
                this.lock();
            }
        },
        messageProperties: {
            name: 'messageProperties',
            header: 'Properties',
            show: {
                header: true,
                columnHeaders: false
            },
            style: 'background-color: #f1f1f1',
            columns: [
                {
                    field: 'key', caption: 'Key', size: '30%', sortable: true,
                    editable: function(record) {
                        return record.editKey ? {type: 'text'} : null;
                    },
                    render: function(record) {
                        return record.editKey ? record.key : '<span style="color: #777777;">' + record.key + '</span>';
                    }
                },
                {
                    field: 'value', caption: 'Value', size: '70%',
                    editable: function(record) {
                        return record.editValue ? {type: 'text'} : null;
                    },
                    render: function(record) {
                        return record.editValue ? record.value : '<span style="color: #777777;">' + record.value + '</span>';
                    }
                }
            ],
            onRender: function(event) {
                this.lock();
            },
            initialise: function(data) {
                // Consider transforming the message properties on the server side
                var properties = $.map(data.properties, function(e1, e2) {
                    return {
                        recid: '_property_' + e2,
                        key: e2,
                        value: e1,
                        editKey: true,
                        editValue: true
                    };
                });
                // Add the 'broker' and 'destination' as custom properties
                properties.push({
                    recid: '_field_broker',
                    key: 'Broker',
                    value: data.broker,
                    editKey: false,
                    editValue: true
                });
                properties.push({
                    recid: '_field_destination',
                    key: 'Destination',
                    value: data.destination,
                    editKey: false,
                    editValue: true
                });
                this.records = properties;
            }
        },
        messageContent: {
            name: 'messageContent',
            formHTML:
            '<div id="message-content" class="w2ui-page">'+
            '    <div class="w2ui-field" style="height: 100%">'+
            '        <textarea id="content" name="content" style="width: 100%; height: 100%; resize: none" />'+
            '    </div>'+
            '</div>'+
            '<div class="w2ui-buttons">'+
            '    <button id="edit" class="w2ui-btn" name="edit">Edit</button>'+
            '    <button id="save" class="w2ui-btn w2ui-btn-green" style="display: none" name="save">Save</button>'+
            '    <button id="cancel" class="w2ui-btn" name="cancel">Cancel</button>'+
            '</div>',
            header: 'Message Content',
            fields: [
                { field: 'content', type: 'textarea', disabled: true }
            ],
            record: {
                content: 'x'
            },
            actions: {
                edit: function () {
                    var form = this;
                    w2ui['messageProperties'].unlock();
                    w2ui['statusHistory'].unlock();
                    form.get("content").disabled = false;
                    form.refresh();
                    document.getElementById("edit").style.display = "none";
                    document.getElementById("save").style.display = "";
                },
                save: function() {
                    // Save and close popup
                    console.log('Changes to properties: ' + JSON.stringify(w2ui['messageProperties'].getChanges()));
                    console.log('Changes to messageContent: ' + JSON.stringify(this.getChanges()));
                    w2popup.close();
                },
                cancel: function () {
                    w2popup.close();
                }
            },
            initialise: function(data) {
                this.record = { content: data.content };
                this.get('content').disabled = true;
            }
        }
    };

    // Initialise the components
    $().w2layout(config.layout);
    $().w2grid(config.statusHistory);
    $().w2grid(config.messageProperties);
    $().w2form(config.messageContent);

});

function displayFailedMessageDetails(failedMessageId) {
    // Currently the Status History has to be loaded from a separate endpoint
    w2ui['statusHistory'].load('/web/api/failed-messages/status-history/' + failedMessageId);
    // Obtain all the details associated with the failedMessageId
    $.getJSON('/web/failed-messages/search/' + failedMessageId)
        .done(function(data) {
            w2ui['messageProperties'].initialise(data);
            w2ui['messageContent'].initialise(data);
            w2popup.open({
                title   : 'Failed Message ' + failedMessageId,
                width   : 800,
                height  : 600,
                showMax : true,
                body    : '<div id="failedMessageDetails" style="position: absolute; left: 0; top: 0; right: 0; bottom: 0;"></div>',
                onOpen  : function (event) {
                    event.onComplete = function () {
                        $('#w2ui-popup').find('#failedMessageDetails').w2render('layout');
                        w2ui.layout.content('left', w2ui['statusHistory']);
                        w2ui.layout.content('main', w2ui['messageProperties']);
                        w2ui.layout.content('bottom', w2ui['messageContent']);
                    }
                },
                onToggle: function (event) {
                    event.onComplete = function () {
                        w2ui.layout.resize();
                    }
                }
            });
        });
}
