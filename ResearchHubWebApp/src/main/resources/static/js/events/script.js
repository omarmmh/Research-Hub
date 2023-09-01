/**
 *
 *  Summary
 *
 *  This javascript file renders and handles the events related to the events / calendar page.
 *  It is split it into four parts.
 *
 *  1. Global Variables.
 *
 *  2. Functions.
 *
 *  3. Event Handlers.
 *
 *  4. Main script. (Where the code begins to execute).
 *
 */


/********************************************************************************************************************
 *
 * 1. Global Variables.
 *
 ********************************************************************************************************************/

/**
 * Init the calendar Popovers.
 * @type {*[]}
 */
const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))
popoverTriggerList.map(function (popoverTriggerEl) {
    return new bootstrap.Popover(popoverTriggerEl)
})

/**
 * The current logged-in user.  And will store the ID of the user only.
 * @type {*|jQuery}
 */
const loggedInUser = $('#_principal').data("principal");

/**
 * Security. Get a valid csrf token and set the header so that the server/API will accept our AJAX requests.
 * @type {*|jQuery}
 */
const token = $('#_csrf').attr('csrf_content');
const header = $('#_csrf_header').attr('csrf_content');
$(document).ajaxSend(function (e, xhr) {
    xhr.setRequestHeader(header, token);
});

/**
 * This Array Maps the Javascript Date Object.
 *
 * Months Array, where the index corresponds to the associated month. Eg, 0 == Jan, 1 == Feb ... Dec == 11.
 * Javascript Date object returns indexes .  getMonth() returns number rather than a string.
 * @type {string[]}
 */
const months = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December",
];


/**
 * Get All Events. ASYNC False.
 * Block execution to ensure we have data to work with to render the page.
 */
let events = [];

/**
 * Get the current date.
 * @type {Date}
 */
let date = new Date();

/**
 * Set the date in the title "events for ... 'date' "
 * @param fullDateString The Date String.
 */
const setEventsForClickedDate = (fullDateString) => $('#events-for-date span').html(fullDateString);

const dateTimeErrorString = "Please check date and time.";


/********************************************************************************************************************
 *
 * 2. Functions.
 *
 ********************************************************************************************************************/

/**
 * Date validation for the event detail modal.
 * A date is considered valid if the start date is below or equal to the end date.
 * An end date cannot have a date that is before the start date.
 *
 * @param eventModel
 * @returns {boolean} True: Valid date, False, Invalid date.
 */
function newEventHasDateErrors(eventModel) {
    if (eventModel['startDate'] > eventModel['endDate']) {
        return true;
    }
    return false;
}

/**
 * Create a Google Maps Element that is centered on a location.
 * @param location : The location to show on the map.
 * @returns {string} : HTML Element.
 */
function getGmapElement(location) {
    const locationClean = location.replace(/\s/g, '%20');
    const element = `<iframe width="100%" height="350px" id="gmap_canvas" src="https://maps.google.com/maps?q=${locationClean}&t=&z=13&ie=UTF8&iwloc=&output=embed"></iframe>`;
    return element;
}

/**
 * Takes a javascript Date object, gets the Locale Time String and slices the seconds..
 * Returns the time formatted to HH:MM
 * @param date : js Date Object.
 * @returns {string} : Time string in format HH:SS
 */
function getFormattedTimeFromDate(date) {
    let d = new Date(date).toLocaleTimeString();
    if(d.includes("M")){
        return d.slice(0, -6);
    }
    return d.slice(0, -3);
}

/**
 * Builds the Time Date String in the event detail modal.
 * Example: Will return a string in the format of: "Sat Mar 11 2023, 02:00 to 18:00"
 * @param event : Event Object / Model./
 * @returns {string} : Formatted Date Time String.
 */
function eventDetailModalDateString(event) {
    let endDateString = getFormattedTimeFromDate(new Date(event.endDate));
    if(new Date(event.endDate).getDate() > new Date(event.startDate).getDate()) {
        endDateString = new Date(event.endDate).toDateString() + " " + getFormattedTimeFromDate(new Date(event.endDate));
    }
    return `${new Date(event.startDate).toDateString()}, ${getFormattedTimeFromDate(event.startDate)} to ${endDateString}`;
}

/**
 * Gets the date modified and formats it.
 * @param event
 * @returns {string}
 */
function dateModified(event){
    let dateMod =new Date(event.dateModified).toDateString() + " "+ getFormattedTimeFromDate(new Date(event.dateModified));
    return `(Last Modified: ${dateMod})`;
}

/**
 * Get all the events from the database and populate the events array.
 */
function getAllEvents() {
    $.ajax({
        type: "GET",
        url: "/api/event",
        dataType: 'json',
        async: false,
        success: function (res) {
            events = res;
            console.log("GET Success: Events: " + events);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 404) {
                // No Events Have been Found
                // Set to an empty array.
                events = []
            } else {
                console.log(thrownError);
            }
        }
    });
}

/**
 * Returns an Array of event objects for a given date.
 *
 * @param date Date object.
 * @returns {*[]} Array of events for the given month
 */
function getEventsForMonth(date) {
    let parseDate;
    const eventsForMonth = [];

    for (let i in events) {
        parseDate = new Date(events[i].startDate);
        if (parseDate.getMonth() === date.getMonth() && parseDate.getFullYear() === date.getFullYear()) {
            eventsForMonth.push(events[i])
        }
    }
    return eventsForMonth
}

/**
 * Get event by ID
 * @param id The Id of the event
 * @returns {*} event object.
 */
function getEventById(id) {
    let event;

    $.ajax({
        type: "GET",
        url: "/api/event/" + id,
        dataType: 'json',
        async: false,
        success: function (res) {
            event = res;
            console.log("GET Success: Events: " + events);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            if (xhr.status == 404) {
                // No Event Has been Found
                event = null;
            } else {
                console.log(thrownError);
            }
        }
    });
    return event;
}

/**
 * Create and Renders the calendar HTML page for any given date.
 */
const renderCalendar = () => {

    getAllEvents();

    const lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
    const prevLastDay = new Date(date.getFullYear(), date.getMonth(), 0).getDate();
    const firstDayIndex = new Date(date.getFullYear(), date.getMonth(), 1).getDay();
    const lastDayIndex = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDay();

    $(".date h1").html(months[date.getMonth()] + " " + date.getFullYear());

    let days = "";
    const nextDays = 7 - lastDayIndex;
    const popOverAttrs = `tabindex="0" data-bs-trigger="focus" data-bs-toggle="popover" title="" content=""`;

    // Find out how many events we have on each date.
    // The numOfEventsOnDay array maps to the days of the calendar.  So index 1 = 1st, index 2 = 2nd... index 30 = 30th
    // of the month.  Initially filled with zeros and the index incremented when a date is found.
    const thisMonthsEvents = getEventsForMonth(date);

    let numOfEventsOnDay = new Array(lastDay + 1).fill(0);
    for (let i = 0; i < thisMonthsEvents.length; i++) {
        const day = new Date(thisMonthsEvents[i].startDate).getDate();
        numOfEventsOnDay[day]++;
    }

    // Generate the last days of the previous month to start the calendar.
    for (let i = firstDayIndex; i > 0; i--) {
        // days += `<div ${popOverAttrs} class="cell prev-date">${(prevLastDay - i) + 1}</div>`;
        days += `<div class="prev-date"></div>`;
    }

    // Generate the days of the current month from day 1 to the last day.
    for (let i = 1; i <= lastDay; i++) {

        let eventsStr = numOfEventsOnDay[i] ? `${numOfEventsOnDay[i]} event${numOfEventsOnDay[i] > 1 ? "s" : ""}` : "";

        if (i === new Date().getDate() && date.getMonth() === new Date().getMonth() && date.getFullYear() === new Date().getFullYear()) {
            days += `<div ${popOverAttrs} class="cell today">${i} <p class="event px-2 py-1 border border-1 rounded bg-light">${eventsStr}</p></div>`;
        } else {
            days += `<div ${popOverAttrs} class="cell">${i}`;
            if (eventsStr != ""){
                days += `<p class="event px-2 py-1 border border-1 rounded bg-light">${eventsStr}</p> </div>`;
            }else{
                days += `<p class="event"></p></div>`;
            }
        }
    }

    // If there are remaining days left on the calendar, and we do not have a complete square design, fill in the remaining
    // space with the first days of the next month.
    if (nextDays > 0 && nextDays <= 7) {
        for (let j = 1; j < nextDays; j++) {
            // days += `<div ${popOverAttrs} class="cell next-date">${j}</div>`;
            days += `<div class="next-date"></div>`;
        }
    }

    // Insert the calculated number of days into the calendar.
    $('.days').html(days);
};

/**
 * Resets the calendar back to 'home'.  The current date.
 */
function resetCalendar() {
    date = new Date();
    renderCalendar();
}

/**
 * Toggles the modal for adding new events.
 */
function toggleAddNewEventModal() {
    $("#addNewEventModal").modal('toggle');
    $("#modal-event-title").val("");
    $("#modal-event-description").val("");
    $("#modal-event-location").val("");
}

/**
 * Toggles Edit Mode in the Event Detail Mode.
 * Edit mode enables the content in the event detail to be editable by setting the content editable
 * attribute to true.
 * If the attribute is true, then we set false to toggle off the editable attribute.
 *
 * If an event (e) is passed.  Then we are entering edit mode.
 * Toggling without passing an event (e) indicates that we are cancelling / leaving edit mode.
 *
 */
function toggleEditMode(e) {
    let value = $('#event-detail-description').attr('contenteditable');
    if (value === 'false' || value === null) {
        value = 'true';
    } else {
        value = 'false';
    }

    if (e != null) {
        const dateTimeString = $("#event-detail-date").text().split(",");
        const dateObj = new Date(dateTimeString[0]);
        const startTime = dateTimeString[1].trim().split(" ")[0];

        let endTime;

        // Get the event to obtain the end date.
        const eventId = $(e.target).data('eventid');
        const event = getEventById(eventId);

        if(new Date(event.endDate).getDate() > new Date(event.startDate).getDate()) {
            endTime = dateTimeString[1].trim().split(" ")[6];
        } else {
            endTime = dateTimeString[1].trim().split(" ")[2];
        }

        const startYear = dateObj.getFullYear();
        let startMonth = dateObj.getMonth() + 1;
        let startDate = dateObj.getDate();

        const endYear = new Date(event.endDate).getFullYear();
        let endMonth = new Date(event.endDate).getMonth() + 1;
        let endDate = new Date(event.endDate).getDate();

        if (startMonth < 10) {
            startMonth = "0" + startMonth;
        }
        if (startDate < 10) {
            startDate = "0" + startDate;
        }
        if (endMonth < 10) {
            endMonth = "0" + endMonth;
        }
        if (endDate < 10) {
            endDate = "0" + endDate;
        }

        // Set the date time input.
        const startDateVal = startYear + "-" + startMonth + "-" + startDate + `T${startTime}`;
        const endDateVal = endYear + "-" + endMonth + "-" + endDate + `T${endTime}`;

        $("#event-detail-edit-startdate").val(startDateVal);
        $("#event-detail-edit-enddate").val(endDateVal);

        console.log(startDateVal);
        console.log(endDateVal);
    }
    $("#event-detail-title").attr('contenteditable', value);
    $("#event-detail-location").attr('contenteditable', value);
    $("#event-detail-description").attr('contenteditable', value);

    $('#event-detail-date').toggle();
    $('#event-detail-edit-date').toggle();
    $('.edit-event-btn').toggle();
    $('.delete-event-btn').toggle();
    $('.save-event-btn').toggle();
    $('.cancel-edit-mode').toggle();
}

/********************************************************************************************************************
 *
 * 3. Event Handlers.
 *
 ********************************************************************************************************************/

/**
 * Previous Month Button.
 * Go back to a previous month.
 */
$(".prev-month-btn").click(function () {
    date.setMonth(date.getMonth() - 1);
    renderCalendar();
});

/**
 * Next Month Button
 * Go forward to the next month.
 */
$(".next-month-btn").click(function () {
    date.setMonth(date.getMonth() + 1);
    renderCalendar();
});

/**
 * Cell Clicked Handler
 * Trigger PopOver containing the events for that date.
 */
$(document).on('click', '.cell', function (event) {

    event.stopPropagation();
    event.stopImmediatePropagation();

    const dateClicked = $(event.target).text().slice(0, 2).replace(/\D/g, '');
    let cellDate;

    if ($(event.target).hasClass('prev-date')) {
        cellDate = new Date(date.getFullYear(), date.getMonth(), 0);
    } else if ($(event.target).hasClass('next-date')) {
        cellDate = new Date(date.getFullYear(), date.getMonth() + 1);
    } else {
        cellDate = date;
    }

    cellDate.setDate(dateClicked);
    setEventsForClickedDate(cellDate.toDateString())

    const dateSelectedStyles = "date-selected";
    const dateSelectedElement = $('.date-selected');

    if (dateSelectedElement.text() === dateClicked) {
        // Same data has been pressed again, so close the event details div.
        dateSelectedElement.removeClass(dateSelectedStyles);
        return;
    }

    // Remove the previously selected date css styling
    dateSelectedElement.removeClass(dateSelectedStyles);

    // Apply the styling to the newly clicked date.
    $(event.target).addClass(dateSelectedStyles);

    // Create the events detail div content.
    // Filter the events by the date selected and add them to the div
    const eventsForDate = events.filter(d => {
        return (cellDate.toDateString() === new Date(d.startDate).toDateString());
    })

    const title = `<div class="row justify-content-between">
                     <div class="col-10">${cellDate.toDateString()}</div>
                     <div class="col-2 text-end"><span class="popover-dismiss"><i class="fa-solid fa-xmark"></i></span>
                   </div>`;

    let eventsForDateHtml = "<ul class='list-unstyled'>";

    for (let i = 0; i < eventsForDate.length; i++) {
        let starttime= getFormattedTimeFromDate(eventsForDate[i].startDate);
        let endtime= getFormattedTimeFromDate(eventsForDate[i].endDate);
        if(new Date(eventsForDate[i].endDate).getDate() != new Date(eventsForDate[i].startDate).getDate()) {
            endtime= new Date(eventsForDate[i].endDate).toDateString() + " " +getFormattedTimeFromDate(eventsForDate[i].endDate);
        }

        eventsForDateHtml += `<li class="mb-1"><a class="event-detail-link btn btn-light border border-1 text-start" data-event-id="${eventsForDate[i].id}" href="#">${starttime} - ${endtime}</br> <p class="event-detail-link fw-bold" data-event-id="${eventsForDate[i].id}">${eventsForDate[i].title}</p></a></li>`;

    }

    // If a cell date is clicked that is in the past, then we will not give the user to the option to add a date.
    // Adding a date to the past seems pointless.
    // If the date is today, or in the future, we will a button so that the user can add a new event.
    const currentDate = new Date();
    if (currentDate < cellDate || $(event.target).hasClass('today')) {
        eventsForDateHtml += `</ul>
        <div class="d-flex justify-content-end">
            <div data-date="${cellDate.getDate()}" data-month="${cellDate.getMonth()}" data-year="${cellDate.getFullYear()}" id="add-new-event-button" class="btn btn-outline-primary btn-sm">
            <i class='fa-solid fa-plus me-1'></i>Add</div>
        </div>`;
    }

    $(this).popover({
        trigger: "focus",
        html: true,
        sanitize: false,
        title: title,
        content: (eventsForDate.length === 0) ? "no events" + eventsForDateHtml : eventsForDateHtml,
    }).popover('show');

});

/**
 * reset the calendar event listener
 */
$("#calendar-reset-btn").click(function () {
    resetCalendar();
});

/**
 * Create a new event form on submit handler.
 * Override the default submit and handle it here.
 */
$("#create-event-form").submit(function (event) {
    event.preventDefault();
    let eventModel = {};
    const principal = $("#_principal").data('principal');

    let formData = $("#create-event-form").serializeArray();
    $(formData).each(function (i, field) {
        const key = field.name;
        let value = field.value;

        if (key === "isVirtual") {
            value = (value === "Virtual");
        }
        eventModel[key] = value;
    });

    if (newEventHasDateErrors(eventModel)) {
        showToastError(dateTimeErrorString);
        return;
    }

    eventModel['organiserId'] = principal;
    eventModel['dateModified'] = new Date();

    $.ajax({
        type: "POST",
        url: "/api/event",
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        data: JSON.stringify(eventModel),
        success: function (res) {
            events = res;
            showToastSuccess("A new event has been added.");
            renderCalendar();
        },
        error: function (e) {
            console.log(e);
        }
    });
    toggleAddNewEventModal();
});

/**
 * Handles the Close Button of the Popover.
 * On-click we close the popover and deselect the div that pressed.
 */
$(document).on('click', '.popover-dismiss', function () {
    console.log("Clicked Close!")
    $(this).parents(".popover").popover('hide');
    $(".cell.date-selected").click();
    $(".date-selected").toggleClass("date-selected");
})


$(document).on('click', '#add-new-event-button', function () {
    $('.popover-dismiss').click();

    let date = $(this).data("date");
    let month = $(this).data("month") + 1;
    const year = $(this).data("year");

    if (date < 10) {
        date = "0" + date
    }
    if (month < 10) {
        month = "0" + month
    }

    const fullDate = year + "-" + month + "-" + date + "T12:00";

    $("#addNewEventModalTitle").html(new Date(fullDate).toDateString())
    $("#start-date").val(fullDate);
    $("#end-date").val(fullDate);
    $("#modal-event-location").val("");
    toggleAddNewEventModal();
});

/**
 * Override the default action when a popover is clicked.
 */
$('body')
    .on('mousedown', '.popover', function (e) {
        e.preventDefault()
    });


/**
 * Events Detail Modal Handler.
 */
$(document).on('click', '.event-detail-link', function (e) {
    e.preventDefault();
    $('.popover-dismiss').click();

    const eventId = $(e.target).data('event-id');
    const event = events.find(obj => obj.id === eventId);

    const titleElement = `${event.title}`;

    // Set the initial location image to a virtual image and string.
    let locationElement = `<svg class="is-virtual-svg my-5" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1" id="Layer_1" x="0px" y="0px" viewBox="0 0 122.88 70.51" style="enable-background:new 0 0 122.88 70.51" xml:space="preserve"><style type="text/css">.st0{fill-rule:evenodd;clip-rule:evenodd;}</style><g><path class="st0" d="M2.54,65.44h12.59c-0.93-0.24-1.63-1.1-1.63-2.1V2.17C13.5,0.98,14.48,0,15.67,0h90.97 c1.19,0,2.17,0.98,2.17,2.17v61.16c0,1.01-0.69,1.86-1.63,2.1h13.16c1.4,0,2.54,1.14,2.54,2.54s-1.14,2.54-2.54,2.54H2.54 c-1.4,0-2.54-1.14-2.54-2.54S1.14,65.44,2.54,65.44L2.54,65.44L2.54,65.44z M52.17,25.95c0.39,0.11,0.83,0.06,1.3-0.12L52.7,21.4 c0.29-1.11,0.74-1.98,1.33-2.62c0.62-0.66,1.39-1.08,2.32-1.25c1.22-0.09,1.59,0.81,2.81,1.59c3.72,2.38,6.86,3.18,11.46,3.23 l-0.95,3.84c0.3,0.13,0.67,0.17,1.03,0.1c0.74-0.06,1.18,0,1.3,0.24c0.17,0.35,0.01,1.06-0.5,2.23l-2.51,4.13 c-0.93,1.53-1.88,3.07-3.07,4.19c-1.14,1.07-2.55,1.78-4.47,1.78c-1.77,0-3.12-0.69-4.22-1.7c-1.16-1.06-2.09-2.5-2.98-3.92 L52,29.68h0l-0.01-0.02c-0.68-1.01-1.03-1.88-1.05-2.54c-0.01-0.22,0.03-0.41,0.1-0.56c0.06-0.13,0.16-0.25,0.29-0.34 C51.53,26.08,51.81,25.99,52.17,25.95L52.17,25.95L52.17,25.95z M54.22,42.66l3.99,8.22l2.01-3.45l-0.98-1.08 c-0.44-0.65-0.54-1.21-0.29-1.7c0.53-1.05,1.64-0.86,2.67-0.86c1.08,0,2.41-0.2,2.75,1.15c0.11,0.45-0.03,0.93-0.35,1.41 l-0.98,1.08l2.01,3.45l3.62-8.22c2.61,2.35,10.33,2.82,13.2,4.42c0.91,0.51,1.73,1.15,2.39,2.02l2.38,5.31H36.25 c0.48-2.88,0.63-2.99,2.38-5.31c0.66-0.87,1.48-1.52,2.39-2.02C43.9,45.47,51.62,45,54.22,42.66L54.22,42.66L54.22,42.66z M72.15,24.97l0.14-5.71c-0.17-2.35-0.95-4.13-2.18-5.47c-3.04-3.29-8.72-4.14-13.01-2.59c-0.72,0.26-1.41,0.59-2.03,0.99 c-1.77,1.13-3.2,2.77-3.77,4.8c-0.13,0.48-0.23,0.96-0.28,1.45c-0.09,2-0.04,4.38,0.1,6.29c-0.21,0.08-0.41,0.18-0.58,0.29 c-0.36,0.24-0.62,0.55-0.8,0.92c-0.16,0.35-0.24,0.76-0.23,1.2c0.03,0.93,0.45,2.05,1.28,3.28l2.23,3.55 c0.94,1.5,1.93,3.02,3.23,4.21c1.35,1.23,3,2.07,5.18,2.08c2.34,0.01,4.05-0.86,5.44-2.16c1.34-1.25,2.34-2.87,3.32-4.49l2.54-4.19 c0.02-0.03,0.03-0.05,0.04-0.08l0,0c0.7-1.6,0.85-2.72,0.48-3.47C73.04,25.42,72.67,25.13,72.15,24.97L72.15,24.97L72.15,24.97z M17.21,3.4h88.19v59.32H17.21V3.4L17.21,3.4z M57.87,66.39h7.14c0.67,0,1.22,0.55,1.22,1.22s-0.55,1.22-1.22,1.22h-7.14 c-0.67,0-1.22-0.55-1.22-1.22C56.65,66.94,57.2,66.39,57.87,66.39L57.87,66.39L57.87,66.39z"/></g></svg>`;
    let locationStr = `<a href="${event.location}">${event.location}</a>`;

    // If the event is not virtual, change the element and string to google maps, and the location.
    if (!event.isVirtual) {
        locationStr = event.location;
        locationElement = `<div class="mapouter mb-3">
                                <div class="gmap_canvas">
                                    ${getGmapElement(locationStr)}
                                </div>
                           </div>`
    }

    // Create the body of the event detail modal.
    let bodyElement = `
        ${locationElement}
        <p contenteditable="false" id="event-detail-date" class="fw-bold">
            ${eventDetailModalDateString(event)}
        </p>
        <p class="text-muted">
        ${dateModified(event)}
        </p>
        <div id="event-detail-edit-date">
            <input id="event-detail-edit-startdate" class="form-select" name="startDate" type="datetime-local" placeholder="start date" required>
            <input id="event-detail-edit-enddate" class="form-select" name="endDate" type="datetime-local" placeholder="end date date" required> 
        </div>
        <hr>
        
        <p class="my-2 fw-bold">Location:</p> 
        <p class="my-2" contenteditable="false" id="event-detail-location">${locationStr}</p>
        <hr>
        
        <p class="my-2 fw-bold">Details:</p>
        <p contenteditable="false" id="event-detail-description">${event.description}</p>
        <hr>
        <div class="d-flex justify-content-between my-2">
            <div>
                <p class="fw-bold">Organiser:</p>
                <span id="event-detail-organiser">${event.organiser}</span>
            </div>
            <div id="eventInterest">
                ${isUserLoggedIn(event, eventId)}
            </div>
        </div>`

    // Create a Footer Element for the event details modal.
    const footerElement = `<div class="text-end text-dark">
                                        <i class="edit-event-btn fa-solid fa-pencil fa-xl" data-eventid="${eventId}" ></i>

                                    <span class="delete-event-btn me-3 text-danger" style="display: none;"
                                       data-eventid="${eventId}">delete</span>

                                    <span class="save-event-btn me-3 text-success" data-eventid="${eventId}"
                                       style="display: none;">save</span>

                                    <span style="display: none;" data-eventid="${eventId}"
                                       class="cancel-edit-mode">cancel</span>
                                    </div>`

    // If the owner of the event is the one viewing the event, then display the footer with edit button.
    // If not, a close button is displayed instead.
    let footer = $("#event-detail-footer");
    if (loggedInUser === event.organiserId) {
        footer.html(footerElement);
    } else {
        footer.html(`<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>`);
    }



    // Set the elements then display the modal.
    $("#event-detail-title").html(titleElement);
    $("#event-detail-body").html(bodyElement);
    $("#event-detail-title").attr('contenteditable', false);
    $("#event-detail-location").attr('contenteditable', false);
    $("#event-detail-description").attr('contenteditable', false);
    $('#eventDetailModal').modal('toggle');
})

/**
 * Checks if the user that's logged in is not the event organiser if not then it displays the button which could be red or blue.
 * @param event The event described in the modal.
 * @param eventId the events id
 * @returns {string}
 * @author jwm22
 */
function isUserLoggedIn(event, eventId){

    let items;
    let interest;

    $.ajax({
        type: "GET",
        url: "/api/event/"+eventId+"/interest",
        dataType: 'json',
        async: false,
        success: function(res){
            interest = res;
        },
        error: function (){
            showToastError("Server Error!")
        }
    })
    if (event.organiserId != loggedInUser) {
        if (interest) {
            items = `
            <button id="unInterested" class="btn btn-danger mt-2" 
            type="submit" data-eventid="${eventId}">
            Not Interested</button>`;
        } else {
            items = `
            <button id="interested" class="btn btn-primary mt-2" 
            type="submit" data-eventid="${eventId}">
            Interested</button>`;
        }
        return items;
    }
    return "";
}

/**
 * If user has clicked the button then change the button in real time.
 * @param test
 */
function buttonAppear(status){
    if (status) {
        $('#interested').attr('id', 'unInterested');
        $('#unInterested').removeClass("btn btn-primary mt-2");
        $('#unInterested').addClass("btn btn-danger mt-2");
        $('#unInterested').text("Not Interested");
    } else {
        $('#unInterested').attr('id', 'interested');
        $('#interested').removeClass("btn btn-danger mt-2");
        $('#interested').addClass("btn btn-primary mt-2");
        $('#interested').text("Interested");
    }
}





/**
 * Add user to the going list
 */
$(document).on('click', '#interested',function (){
    $.ajax({
        type: "POST",
        url: "/api/event/user/" + $(this).data("eventid"),
        success: function () {
            showToastSuccess("Added to Event Interest List!");
        },
        error: function () {
            showToastError("Server Error!")
        }
    });

    buttonAppear(true);

});

/**
 * Remove user from going list
 */
$(document).on('click', '#unInterested', function (){
    $.ajax({
        type: "DELETE",
        url: "/api/event/delete/interest/" +  $(this).data('eventid'),
        success: function () {
            showToastSuccess("Removed from Event Interest List!");
        },
        error: function () {
            showToastError("Server Error!")
        }
    });

    buttonAppear(false);
});


/**
 * Edit Button Clicked Handler.
 */
$(document).on('click', '.edit-event-btn', function (e) {
    toggleEditMode(e);
});

/**
 * Cancel Edit Mode Button Clicked Handler.
 */
$(document).on('click', '.cancel-edit-mode', function () {
    const eventId = $(this).data('eventid')
    const event = getEventById(eventId);

    $("#event-detail-title").text(event.title)
    $("#event-detail-description").text(event.description);
    $("#event-detail-location").text(event.location);

    const dateElement = $('#event-detail-date');
    dateElement.text(eventDetailModalDateString(event))

    toggleEditMode();
    showToastInfo("No changes have been made.")
})

/**
 * Save Event Button Clicked Handler.
 */
$(document).on('click', '.save-event-btn', function () {
    let eventModel = {};
    const principal = $("#_principal").data('principal');

    eventModel['id'] = $(this).data("eventid");
    eventModel['title'] = $("#event-detail-title").text();
    eventModel['description'] = $("#event-detail-description").text();
    eventModel['startDate'] = new Date($("#event-detail-edit-startdate").val());
    eventModel['endDate'] = new Date($("#event-detail-edit-enddate").val());
    eventModel['location'] = $("#event-detail-location").text();
    eventModel['organiserId'] = principal;
    eventModel["dateModified"] = new Date();

    let hasErrors = false;
    if (newEventHasDateErrors(eventModel)) {
        showToastError(dateTimeErrorString)
        hasErrors = true;
    }
    for (const [k, v] of Object.entries(eventModel)) {
        if (k === 'title' && v.trim() === '') {
            showToastError("Title cannot be empty");
            hasErrors = true;
        }
        if (k === 'description' && v.trim() === '') {
            showToastError('Details cannot be empty');
            hasErrors = true
        }
        if (k === 'location' && v.trim() === '') {
            showToastError('Location cannot be empty');
            hasErrors = true
        }
    }

    let updatedEvent;
    if (!hasErrors) {
        $.ajax({
            type: "PUT",
            url: "/api/event/" + $(this).data("eventid"),
            contentType: "application/json; charset=utf-8",
            dataType: 'json',
            data: JSON.stringify(eventModel),
            success: function (res) {
                updatedEvent = res;
                $("#gmap_canvas").replaceWith(getGmapElement(updatedEvent.location));
                $("#event-detail-title").text(updatedEvent.title);
                $("#event-detail-date").text(eventDetailModalDateString(updatedEvent));
                $("#event-detail-description").text(updatedEvent.description);
                showToastSuccess("Event updated.");
                toggleEditMode();
                renderCalendar();
            },
            error: function (e) {
                console.log(e);
                showToastError("Server error")
            }
        });
    }

})

/**
 * Delete Event Button Handler
 */
$(document).on('click', '.delete-event-btn', function (e) {

    const eventId = $(e.target).data('eventid');
    const result = confirm("Are you sure you want to delete this event?");

    if (result) {
        $.ajax({
            type: "DELETE",
            url: "/api/event/" + eventId,
            success: function (res) {
                toggleEditMode();
                showToastSuccess(res);
                renderCalendar();
            },
            error: function (e) {
                console.log(e);
            }
        });
        $('#eventDetailModal').modal('toggle');
    }
})

/**
 * Show the location input field if the user selects an in person event type.
 */
$("select").change(function () {
    $(this).find("option:selected").each(function () {
        const optionValue = $(this).text();
        if (optionValue === "Virtual") {
            $("#modal-event-location").prop('placeholder', "URL");
        } else {
            $("#modal-event-location").prop('placeholder', "Address");
        }
    })
});

/********************************************************************************************************************
 *
 * 4. Main script.
 *
 ********************************************************************************************************************/

// Initial Rendering of the calendar.
renderCalendar();