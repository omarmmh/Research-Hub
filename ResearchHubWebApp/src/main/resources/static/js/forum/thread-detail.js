/**
 * Toggles the add commet form to visible or not visible.
 */
function toggleForm() {
    $("#add-comment-form").slideToggle();
    $("#comment-submit-btn").toggle();
}

/**
 * Security. Get a valid csrf token and set the header so that the server will accept our AJAX requests.
 * @type {*|jQuery}
 */
const token = $('#_csrf').attr('csrf_content');
const header = $('#_csrf_header').attr('csrf_content');
$(document).ajaxSend(function (e, xhr, options) {
    xhr.setRequestHeader(header, token);
});

/**
 * Click event handler for the add comment button which will animate and reveal a text editor on the page.
 */
$("#add-comment").click(function () {
    tinymce.get("statictinymce").setContent("");
    $(this).text($(this).text() === "Add new comment" ? "Cancel" : "Add new comment");
    $(this).toggleClass("btn-red");
    $(this).blur();
    toggleForm();
});

/**
 * Submits a new comment to the server via an AJAX post request and handles the return.
 */
$("#comment-submit-btn").click(function () {
    const threadId = this.getAttribute("thread");
    const comment = tinymce.get("statictinymce").getContent();
    const charCount = tinymce.activeEditor.plugins.wordcount.body.getCharacterCount();
    if (!comment.trim()) {
        showToastError("Comment cannot be empty");
        return;
    }if(charCount > 1000){
        showToastError("Comment can only be 1000 characters long!");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/api/forum/add-comment",
        data: {
            "commentId": null,
            "threadId": threadId,
            "body": comment,
            "UserName": null
        },
        success: function (data) {
            const name = data.UserName;
            const id = data.commentId;
            showToastSuccess("Your comment has been added");
            tinymce.get("statictinymce").setContent("");
            $("#comments").last().append(`
                        <div id="${id}" class="card mt-4">
                            <div class="card-header">
                                <div class="row align-items-center justify-content-between">
                                    <div class="col">
                                        <a class="text-decoration-none" href="/profile/${name}">
                                            <h5 id="title-${id}">${name}</h5>
                                        </a>
                                    </div>
                                    <div class="col">
                                        <div class="text-end">${data.date}</div>
                                    </div>
                                </div>
                            </div>
                                                  
                            <div class="card-body">
                                <div class="row justify-content-start">
                                    <div class="col-sm-1 text-center">
                                        <i data-vote-type="up" class="vote-btn fa-solid fa-caret-up fa-2xl" comment-id="${id}"></i>
                                        <p style="min-width: 20px" id="votecount-${id}">0</p>
                                        <i data-vote-type="down" class="vote-btn fa-solid fa-caret-down fa-2xl" comment-id="${id}"></i>
                                    </div>
                                    <div class="col ms-1">
                                        <div id="body-${id}">${comment}</div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="card-footer">
                                 <div class="row align-items-center justify-content-between text-light">
                                    .
                                    <div class="col text-end text-dark">
                                        <i class="edit-btn fa-solid fa-pencil fa-xl" comment="${id}" ></i>

                                    <span class="delete-comment-btn me-3 text-danger" style="display: none;"
                                       comment="${id}">delete</span>

                                    <span class="save-comment-btn me-3 text-success" comment="${id}"
                                       style="display: none;">save</span>

                                    <span style="display: none;" comment="${id}"
                                       class="cancel-edit-mode">cancel</span>
                                    </div>
                                </div>
                            </div>
                        </div>`
            );
            if ($("#no-comments").length) {
                $("#no-comments").remove();
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
    toggleForm();
    $("#add-comment").toggleClass("btn-red");
    $("#add-comment").text($(this).text() === "Add new comment" ? "Cancel" : "Add new comment");
});


/**
 * Validate input fields that the user has input data into.
 *
 * @param body : Text input field of the comment
 * @returns {boolean} : True if inputs are valid, else False (invalid)
 */
function validateFields(body, charCount) {
    if (!body.trim()) {
        showToastError("Comment text cannot be empty");
        return false;
    }
    if (charCount > 1000){
        showToastError("Comment can only be 1000 characters long!");
        return false;
    }

    return true;
}

/**
 * Turn off the editable element so that the user cannot change the comment data.
 *
 * @param commentId : Comment element ID to deactivate.
 */
function deactivateEditable(commentId) {
    tinymce.remove("#body-" + commentId);
    $("#body-" + commentId).attr('contenteditable', 'false');
}

/**
 * Toggle the comment management buttons visibility (edit/save/delete).
 * Displayed or not displayed on the current page.
 * @param commentId : IntegerID value representing the comment element want to toggle the buttons for.
 */
function toggleThreadManagerButtons(commentId) {
    $(".delete-comment-btn[comment=" + commentId + "]").toggle();
    $(".save-comment-btn[comment=" + commentId + "]").toggle();
    $(".cancel-edit-mode[comment=" + commentId + "]").toggle();
}

/**
 * Show or Hide the confirmation delete modal.
 *
 * @param title : The title/header to show in the modal view.
 * @param commentId : Comment ID of the thread marked for deletion.
 */
function toggleModal(body, commentId) {
    $("#comment-body").text(body);
    $("#confirm-delete-comment-btn").attr("comment", commentId);
    $("#exampleModalCenter").modal('toggle');
}

/**
 * Closes the current modal that is displayed.
 */
$("#modal-close").click(function () {
    toggleModal(null, null);
});

/**
 * Delete comment button click event.  When clicked, a confirmation modal is displayed.
 */
$("body").on('click', ".delete-comment-btn", function () {
    const commentId = this.getAttribute("comment");
    const body = $("#body-" + commentId).text();
    toggleModal(body.length > 120 ? body.slice(0, 120) + "..." : body, commentId);
});

/**
 * Confirm Delete button in the modal.  When clicked, an AJAX post query is made to the server so that the comment can
 * be removed from the database.  On success, the comment will then be removed from the DOM.
 */
$("#confirm-delete-comment-btn").click(function () {
    const commentId = this.getAttribute("comment");
    $.ajax({
        type: "POST",
        url: "/api/forum/delete-comment",
        data: {
            "id": commentId
        },
        success: function () {
            $(`#${commentId}`).remove();
            toggleModal(null, null);
            showToastSuccess("Your comment has been removed.");
        },
        error: function (error) {
            console.log(error);
        }
    });
    toggleModal(null, null);
});

/**
 * Performs an AJAX Post query to the server to save the new thread to the database.
 * On success, the new thread is added to the DOM.
 */
$("body").on('click', '.save-comment-btn', function () {
    const commentId = this.getAttribute("comment");
    const body = tinymce.get("body-" + commentId).getContent();
    const charCount = tinymce.activeEditor.plugins.wordcount.body.getCharacterCount();

    if (!validateFields(body,charCount)) {
        return;
    }

    deactivateEditable(commentId);

    $(".edit-btn[comment=" + commentId + "]").toggle();
    toggleThreadManagerButtons(commentId);

    $.ajax({
        type: "POST",
        url: "/api/forum/edit-comment",
        data: {
            "body": body,
            "id": commentId
        },
        success: function () {
            $("#body-" + commentId).html(body);
            showToastSuccess("Your comment has been updated.");
        },
        error: function (error) {
            console.log(error);
        }
    });
});

// Global vars for storing contents of thread title & body so that user actions can be 'undone'
let originalBody = null;

/**
 * When the edit comment button is clicked, the comment management buttons are displayed (delete/save/cancel)
 */
$("body").on('click', '.edit-btn', function () {
    const commentId = this.getAttribute("comment");
    originalBody = $("#body-" + commentId).val();
    $(this).toggle();
    toggleThreadManagerButtons(commentId);
    tinymceInit(commentId);
});


/**
 * Cancels / disables 'edit mode'.  Any changes the user has made will be 'undone' and reverted to the original
 * comment details.  If no changes were made we simple disable edit mode.  If a user has modified the thread, an AJAX
 * call is made to the server to retrieve the original and unmodified comment to update the DOM.
 *
 */
$("body").on('click', '.cancel-edit-mode', function () {
    const commentId = this.getAttribute("comment");
    if ($("#body-" + commentId).text() != originalBody) {
        // Things have changed, update and revert to original
        $.ajax({
            type: "GET",
            url: "/api/forum/get-comment",
            data: {
                "body": "",
                "id": commentId
            },
            success: function (data) {
                $("#body-" + commentId).html(data.body);
            },
            error: function (error) {
                console.log(error);
            }
        });
    }

    showToastInfo("No changes have been made.");
    deactivateEditable(commentId);
    $(".edit-btn[comment=" + commentId + "]").toggle();
    toggleThreadManagerButtons(commentId);
});

$("body").on('click', '.vote-btn', function () {
    const commentId = this.getAttribute("comment-id");
    const voteType = this.getAttribute("data-vote-type");
    $.ajax({
        type: "POST",
        url: "/api/forum/votes",
        data: {
            id: commentId,
            type: voteType
        },
        success: function (data) {
            console.log(data.voteType)
            if (data.voteType === 'cancel') {
                $(".vote-btn[data-vote-type=down][comment-id="+commentId+"]").css('color', 'black');
                $(".vote-btn[data-vote-type=up][comment-id="+commentId+"]").css('color', 'black');
            }
            else if (data.voteType === 'up') {
                $(".vote-btn[data-vote-type=down][comment-id="+commentId+"]").css('color', 'black');
                $(".vote-btn[data-vote-type=" + voteType + "][comment-id="+commentId+"]").css('color', 'green');
            } else if (data.voteType === 'down') {
                $(".vote-btn[data-vote-type=up][comment-id="+commentId+"]").css('color', 'black');
                $(".vote-btn[data-vote-type=" + voteType + "][comment-id="+commentId+"]").css('color', 'red');
            }
            showToastSuccess(data.success)
            $("#votecount-" + commentId).text(data.count);
        },
        error: function (e) {
            showToastError(e.responseJSON.error);
        }
    });
});

