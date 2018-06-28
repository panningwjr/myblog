;(function ($) {
    $.put = function (url, data, success) {
        $.ajax({
            type: "put",
            url: url,
            contentType: "application/json",
            data: JSON.stringify(data),
            dataType: "json",
            success: success
        });
    }

    $.delete = function (url, success) {
        $.ajax({
            type: "delete",
            url: url,
            success: success
        });
    }

    $.post = function (url, data, success) {
        $.ajax({
            type: "post",
            url: url,
            contentType: "application/json",
            data: JSON.stringify(data),
            dataType: "json",
            success: success
        });
    }

})($);
