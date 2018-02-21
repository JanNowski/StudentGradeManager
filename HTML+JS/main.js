(function () {
    "use strict";

    var backendAddress = "http://localhost:8080/";

    var collection = function(url, idAttr) {
        var self = ko.observableArray();

        self.url = url;
        self.postUrl = self.url;

        self.get = function(query) {
            var url = self.url;

            if(query) {
                url = url + query;
            }
            $.ajax({
                url: url,
                dataType: "json",
                accept: "application/json",
                success: function(data) {
                    if(self.sub) {
                        self.sub.dispose();
                    }
                    self.removeAll();
                    data.forEach(function(element, index, array) {
                        var object = ko.mapping.fromJS(element, { ignore: ["link"] });
                        object.links = [];

                        if($.isArray(element.link)) {
                            element.link.forEach(function(link) {
                                object.links[link.params.rel] = link.href;
                            });
                        } else {
                            object.links[element.link.params.rel] = element.link.href;
                        }

                        self.push(object);

                        ko.computed(function() {
                            return ko.toJSON(object);
                        }).subscribe(function() {
                            self.updateRequest(object);
                        });
                    });

                    self.sub = self.subscribe(function(changes) {
                        changes.forEach(function(change) {
                            if(change.status === 'added') {
                                self.saveRequest(change.value);
                            }
                            if(change.status === 'deleted') {
                                self.deleteRequest(change.value);
                            }
                        });
                    }, null, "arrayChange");
                }
            });
        }

        self.saveRequest = function(object) {
            $.ajax({
                url: self.postUrl,
                dataType: "json",
                contentType: "application/json",
                data: ko.mapping.toJSON(object),
                method: "POST",
                success: function(data) {
                    var response = ko.mapping.fromJS(data);
                    object[idAttr](response[idAttr]());
                    object.links = [];

                    if($.isArray(data.link)) {
                        data.link.forEach(function(link) {
                            object.links[link.params.rel] = link.href;
                        });
                    } else {
                        object.links[data.link.params.rel] = data.link.href;
                    }

                    console.log(object.index());
                    ko.computed(function() {
                        return ko.toJSON(object);
                    }).subscribe(function() {
                        self.updateRequest(object);
                    });
                }
            });
        }

        self.updateRequest = function(object) {
            $.ajax({
                url: object.links['self'],
                dataType: "json",
                contentType: "application/json",
                data: ko.mapping.toJSON(object, { ignore: ["links"] }),
                method: "PUT"
            });
        }

        self.deleteRequest = function(object) {
            $.ajax({
                url: object.links['self'],
                method: "DELETE"
            });
        }

        self.add = function(form) {
            var data = {};
            $(form).serializeArray().map(function(x) {
                data[x.name] = x.value;
            });
            data[idAttr] = null;

            self.push(ko.mapping.fromJS(data));
            $(form).each(function() {
                this.reset();
            });
        }

        self.delete = function() {
            self.remove(this);
        }

        self.parseQuery = function() {
            var newValues = {};

            Object.keys(self.queryParams).forEach(function(key) {
                if(!!self.queryParams[key]()) newValues[key] = self.queryParams[key];
            });

            self.get('?' + $.param(ko.mapping.toJS(newValues)));
        }

        return self;
    }

    var model = function() {
        var self = this;

        self.students = new collection(backendAddress + "students", "index");

        students.getGrades = function() {
            window.location = "#student-grades";
            self.grades.selectedStudent({
                name: this.name(),
                surname: this.surname(),
                index: this.index()
            });

            self.grades.selectedCourse(null);
            self.grades.isCourseEnable(true);
            self.grades.isStudentEnable(false);
            self.grades.url = backendAddress + "students/" + this.index() + "/grades";
            self.grades.get();
        };

        self.students.queryParams = {
            index: ko.observable(),
            name: ko.observable(),
            surname: ko.observable(),
            birth: ko.observable()
        };

        Object.keys(self.students.queryParams).forEach(function(key) {
            self.students.queryParams[key].subscribe(function() {
                self.students.parseQuery();
            });
        });

        self.students.get();

        self.courses = new collection(backendAddress + "courses", "courseID");

        self.courses.queryParams = {
            name: ko.observable(),
            courseInstructor: ko.observable()
        }

        Object.keys(self.courses.queryParams).forEach(function(key) {
            self.courses.queryParams[key].subscribe(function() {
                self.courses.parseQuery();
            });
        });

        self.courses.get();

        self.grades = new collection(backendAddress + "grades", "gradeID");

        self.grades.selectedCourse = ko.observable();
        self.grades.selectedStudent = ko.observable({
            name: '',
            surname: '',
            index: ''
        });
        self.grades.isCourseEnable = ko.observable(true);
        self.grades.isStudentEnable = ko.observable(true);

        self.grades.add = function(form) {
            self.grades.postUrl = backendAddress + 'students/' + self.grades.selectedStudent().index + '/grades';
            var data = {};
            $(form).serializeArray().map(function(x) {
                if(x.name === 'name') {
                    data.course = {
                        courseID: x.value
                    };
                }
                else data[x.name] = x.value;
            });
            self.grades.push(ko.mapping.fromJS(data));
            $(form).each(function() {
                this.reset();
            });
        }

        self.grades.queryParams = {
            course: ko.observable(),
            grade: ko.observable(),
            date: ko.observable()
        }

        Object.keys(self.grades.queryParams).forEach(function(key) {
            self.grades.queryParams[key].subscribe(function() {
                self.grades.parseQuery();
            });
        });
    }

    $(document).ready(function() {
        ko.applyBindings(new model());
    });

}());