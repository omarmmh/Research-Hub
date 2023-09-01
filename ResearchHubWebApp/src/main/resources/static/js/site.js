// Wire up the logout link to submit the form.
$(document).ready(function () {
    $('#navbar-logout-btn').click(function (e) {
        e.preventDefault();
        $('#navbar-logout-form').submit();
    });
});

function showToastSuccess(msg) {
    iziToast.show({
        title: "Success",
        titleSize: '18',
        message: msg,
        messageSize: '18',
        position: 'bottomRight',
        color: '#1b6ec2',
        transitionIn: 'bounceInRight',
        progressBar: true,
        progressBarColor: 'black',
        icon: 'fa-solid fa-circle-exclamation',
        theme: 'dark',
        drag: true,
    });
}

function showToastInfo(msg) {
    iziToast.show({
        title: "Info",
        titleSize: '18',
        message: msg,
        messageSize: '18',
        position: 'bottomRight',
        color: '#deb82f',
        transitionIn: 'bounceInRight',
        progressBar: true,
        progressBarColor: 'black',
        icon: 'fa-solid fa-circle-info',
        theme: 'light',
        drag: true,
    });
}

function showToastError(msg) {
    iziToast.show({
        title: "Error",
        titleSize: '18',
        message: msg,
        messageSize: '18',
        position: 'bottomRight',
        color: '#dc3545',
        transitionIn: 'bounceInRight',
        progressBar: true,
        progressBarColor: 'black',
        icon: 'fa-solid fa-circle-info',
        theme: 'dark',
        drag: true,
    });
}

/**
 * Initialise a main/single TinyMCE Text Editor based on selector ID.
 */
tinymce.init({
    selector: '#statictinymce',
    valid_elements: 'a[href|target=_blank],strong/b,div[align],br,p',
    branding: false,
    plugins: 'link, wordcount',
    toolbar: 'link',
});

/**
 * Initialise another instance of the tinyMCE editor and set its id attribute.
 * @param id : editor_id to set the id field in the HTML page.
 */
// Init TinyMCE
function tinymceInit(id) {
    tinymce.init({
        selector: "#body-" + id,
        valid_elements: 'a[href|target=_blank],strong/b,div[align],br,p',
        branding: false,
        plugins: 'link, wordcount',
        toolbar: 'link',
    });
}
