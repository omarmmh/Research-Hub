/**
 * Get a CSRF token which will allow our AJAX queries to communicate with the server.
 * The server has crsf protection enabled, so without a valid token our request will be automatically rejected
 * by the server.
 *
 * The CSRF Token will then be set for all our AJAX queries relating to forum thread management.
 *
 * @type {*|jQuery}
 */
const token = $('#_csrf').attr('csrf_content');
const header = $('#_csrf_header').attr('csrf_content');
$(document).ajaxSend(function (e, xhr, options) {
    xhr.setRequestHeader(header, token);
});


/**
 * Validate input fields that the user has input data into.
 *
 * @param title : Thread title input field
 * @param body : Thread body input field
 * @returns {boolean} : True if inputs are valid, else False (invalid)
 */
function validateFields(title, body,titleLength, bodyLength) {
    if (!title.trim() && !body.trim()) {
        showToastError("Title cannot be empty");
        showToastError("Thread text cannot be empty");
        return false;
    }

    if(titleLength > 200 && !body.trim()){
        showToastError("Thread text cannot be empty");
        showToastError("Title can only be up-to 200 characters!")
        return false;
    }

    if (!title.trim() && bodyLength > 20000) {
        showToastError("Title cannot be empty");
        showToastError("Exceeded thread body size of 20,000 characters!")
        return false;
    }

    if(titleLength > 200 && bodyLength > 20000){
        showToastError("Title can only be up-to 200 characters!")
        showToastError("Exceeded thread body size of 20,000 characters!")
        return false;
    }

    if (!title.trim()) {
        showToastError("Title cannot be empty");
        return false;
    }

    if (!body.trim()) {
        showToastError("Thread text cannot be empty");
        return false;
    }

    if(titleLength > 200){
        showToastError("Title can only be up-to 200 characters!")
        return false;
    }

    if(bodyLength > 20000){
        showToastError("Exceeded thread body size of 20,000 characters!")
        return false;
    }

    return true;
}

/**
 * Turn off the editable element so that the user cannot change the thread data.
 *
 * @param threadId : Thread element ID to deactivate.
 */
function deactivateEditable(threadId) {
    tinymce.remove("#body-" + threadId);
    $("#body-" + threadId).attr('contenteditable', 'false');
    $("#title-" + threadId).attr('contenteditable', 'false');
}

/**
 * Toggle the thread management buttons visibility (edit/save/delete).
 * Displayed or not displayed on the current page.
 * @param threadId : The thread we want to toggle the buttons for.
 */
function toggleThreadManagerButtons(threadId) {
    $(".delete-thread-btn[thread=" + threadId + "]").toggle();
    $(".save-thread-btn[thread=" + threadId + "]").toggle();
    $(".cancel-edit-mode[thread=" + threadId + "]").toggle();
}

/**
 * Show or Hide the confirm delete modal.
 *
 * @param title : The title/header to show in the modal view.
 * @param threadId : Thread ID of the thread marked for deletion.
 */
function toggleModal(title, threadId) {
    $("#thread-title").text(title);
    $("#confirm-delete-thread-btn").attr("thread", threadId);
    $("#exampleModalCenter").modal('toggle');
}

/**
 * Closes the current modal that is displayed.
 */
$("#modal-close").click(function () {
    toggleModal(null, null);
})

/**
 * Confirm Delete button in the modal.  When clicked, an AJAX post query is made to the server so that the thread can
 * be removed from the database.  On success, the thread will then be removed from the DOM.
 */
$("#confirm-delete-thread-btn").click(function () {
    const threadId = this.getAttribute("thread");
    $.ajax({
        type: "POST",
        url: "/api/forum/delete-thread",
        data: {
            "id": threadId
        },
        success: function () {
            $(`#${threadId}`).remove();
            toggleModal(null, null);
            showToastSuccess("Your thread has been removed.");
        },
        error: function (error) {
            console.log(error);
        }
    });
});

/**
 * Displays a text editor to create a new thread.
 */
$("#new-thread-btn").click(function () {
    $(this).toggleClass("btn-red");
    $(this).blur();
    $("#new-thread-form").slideToggle();
    $(this).text($(this).text() === "New thread" ? "Cancel" : "New thread");
    $("#thread-submit-btn").toggle();
});

/**
 * Submits the new thread form to the server to handle.
 */
$("#thread-submit-btn").click(function () {
    const title = $("#form-input-title").val();
    const body = tinymce.get("statictinymce").getContent();
    const charCount = tinymce.activeEditor.plugins.wordcount.body.getCharacterCount();
    if (!validateFields(title, body,title.length, charCount)) {
        return;
    }
    $("#new-thread-form").submit();
});

/**
 * Delete thread button click event.  When clicked, a confirmation modal is displayed.
 */
$(".delete-thread-btn").click(function () {
    const threadId = this.getAttribute("thread");
    const title = $("#title-" + threadId).text();
    toggleModal(title, threadId);
});

/**
 * Performs an AJAX Post query to the server to save the new thread to the database.
 * On success, the new thread is added to the DOM.
 */
$(".save-thread-btn").click(function () {
    const threadId = this.getAttribute("thread");
    const title = $("#title-" + threadId).text();
    const body = tinymce.get("body-" + threadId).getContent();
    document.getElementById(threadId).classList.add("highlight-card");
    document.getElementById("link-" +threadId).classList.add("stretched-link");
    const charCount = tinymce.activeEditor.plugins.wordcount.body.getCharacterCount();


    if (!validateFields(title, body, title.length, charCount)) {
        if (!title.trim()) {
            $("#title-" + threadId).focus();
        }
        return;
    }

    deactivateEditable(threadId);

    $(".edit-btn[thread=" + threadId + "]").toggle();
    toggleThreadManagerButtons(threadId);

    $.ajax({
        type: "POST",
        url: "/api/forum/edit-thread",
        data: {
            "title": title,
            "body": body,
            "id": threadId
        },
        success: function () {
            if (body.length > 300) {
                $("#body-" + threadId).html(body.slice(0, 300) + "...");
            } else {
                $("#body-" + threadId).html(body);
            }
            showToastSuccess("Your thread has been updated.");

        },
        error: function (error) {
            console.log(error);
        }
    });
});

// Global vars for storing contents of thread title & body so that user actions can be 'undone'
let originalTitle = null;
let originalBody = null;

/**
 * When the edit thread button is clicked, the thread management buttons are displayed (delete/save/cancel)
 */
$(".edit-btn").click(function () {
    const threadId = this.getAttribute("thread");
    document.getElementById(threadId).classList.remove("highlight-card");
    document.getElementById("link-" +threadId).classList.remove("stretched-link");

    originalTitle = $("#title-" + threadId).text();
    originalBody = $("#body-" + threadId).val();

    $(this).toggle();
    toggleThreadManagerButtons(threadId);
    $("#title-" + threadId).attr('contenteditable', 'true').focus();
    tinymceInit(threadId);

    if (originalBody.length > 300) {
        $.ajax({
            type: "GET",
            url: "/api/forum/get-thread",
            data: {
                "title": "",
                "body": "",
                "id": threadId
            },
            success: function (data) {
                $("#body-" + threadId).text(data.body);
            },
            error: function (error) {
                console.log(error);
            }
        });
    }
});

/**
 * Cancels / disables 'edit mode'.  Any changes the user has made will be 'undone' and reverted to the original
 * thread details.  If no changes were made we simple disable edit mode.  If a user has modified the thread, an AJAX
 * call is made to the server to retrieve the original and unmodified thread to update the DOM.
 *
 */
$(".cancel-edit-mode").click(function () {
    const threadId = this.getAttribute("thread");

    document.getElementById(threadId).classList.add("highlight-card");
    document.getElementById("link-" +threadId).classList.add("stretched-link");

    if ($("#title-" + threadId).text() != originalTitle || $("#body-" + threadId).text() != originalBody) {
        // Things have changed, update and revert to original
        $.ajax({
            type: "GET",
            url: "/api/forum/get-thread",
            data: {
                "title": "",
                "body": "",
                "id": threadId
            },
            success: function (data) {
                $("#title-" + threadId).text(data.title);
                if (data.body.length > 300) {
                    $("#body-" + threadId).html(data.body.slice(0, 300) + "...");
                } else {
                    $("#body-" + threadId).html(data.body);
                }
            },
            error: function (error) {
                console.log(error);
            }
        });
    }

    showToastInfo("No changes have been made.");
    deactivateEditable(threadId);
    $(".edit-btn[thread=" + threadId + "]").toggle();
    toggleThreadManagerButtons(threadId);
});