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
            ]
        },
        messageProperties: {
            name: 'messageProperties',
            header: 'Properties',
            show: {
                header: true,
                columnHeaders: false
            },
            columns: [
                { field: 'key', caption: 'Key', size: '30%', sortable: true },
                { field: 'value', caption: 'Value', size: '70%' }
            ]
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
                content: '{ foo: "bar" }'
            },
            actions: {
                edit: function () {
                    var form = this;
                    if (form.get("content").disabled) {
                        form.get("content").disabled = false;
                        form.refresh();
                        document.getElementById("edit").style.display = "none";
                        document.getElementById("save").style.display = "";
                    } else {
                        // Save and close popup
                    }
                },
                cancel: function () {
                    w2popup.close();
                }
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
            // Consider transforming the message properties on the server side?
            w2ui['messageProperties'].records = $.map(data.properties, function(e1,e2) {
                return {key:e2, value:e1};
            });
            w2ui['messageContent'].record = { 'content': data.content };
            w2ui['messageContent'].get('content').disabled = true;
        });

    w2popup.open({
        title   : 'Failed Message ' + failedMessageId,
        width   : 800,
        height  : 600,
        showMax : true,
        body    : '<div id="failedMessageDetails" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;"></div>',
        onOpen  : function (event) {
            event.onComplete = function () {
                $('#w2ui-popup #failedMessageDetails').w2render('layout');
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
}
