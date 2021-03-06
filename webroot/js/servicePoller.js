// Get Service Urls.
let servicesRequest = new Request('/urls');
fetch(servicesRequest)
    .then(function(response) {
        return response.json();
    })
    .then(function(serviceList) {
        const listContainer = document.querySelector('#service-list');
        if (serviceList.length) {
            listContainer.append(ServicePollerWidget.createHeader());
        }
        serviceList.forEach(service => {
            var li = document.createElement("li");
            li.appendChild(ServicePollerWidget.createElement("div", service.name, [{
                name: "class",
                value: "name"
            }]));
            li.appendChild(ServicePollerWidget.createElement("div", service.url, [{
                name: "class",
                value: "url"
            }]));
            li.appendChild(ServicePollerWidget.createElement("div", service.status, [{
                name: "class",
                value: "status"
            }]));
            li.appendChild(ServicePollerWidget.createElement("div", service.updated, [{
                name: "class",
                value: "date"
            }]));
            listContainer.appendChild(li);
        });
    });

const saveButton = document.querySelector('#save-service');
saveButton.onclick = function() {
    ServicePollerWidget.insertServiceUrl();
};

// ServicePollerWidget functions.
var ServicePollerWidget = {
    createHeader: function() {
        let li = document.createElement("li");
        let headerList = [
            ["Service Name", "name"],
            ["Service Url", "url"],
            ["Service Status", "status"],
            ["Last Updated Date", "date"]
        ];
        li.setAttribute("class", "li-header");
        headerList.forEach(x => {
            li.appendChild(ServicePollerWidget.createElement("div", x[0], [{
                name: "class",
                value: x[1]
            }]));
        })
        return li;
    },
    createElement: function(name, text, attributes, eventFunction) {
        let element = document.createElement(name);
        attributes.forEach(x => {
            element.setAttribute(x.name, x.value);
        });
        element.append(document.createTextNode(text));
        if (eventFunction) {
            eventFunction(element);
        }
        return element;
    },
    isValidUrl: function(str) {
        var res = str.match(/(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/g);
        return (res !== null)
    },
    insertServiceUrl: function () {
        let serviceName = document.querySelector('#service-name').value;
        let serviceUrl = document.querySelector('#url-name').value;

        if (!ServicePollerWidget.isValidUrl(serviceUrl)) {
            alert("Invalid Url is entered!");
            return;
        }

        if (serviceUrl.length) {
            let insertRequest = new Request('/insert');
            fetch(insertRequest, {
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        name: serviceName,
                        url: serviceUrl
                    })
                })
                .then(function() {
                    location.reload();
                })
        }
    }
};