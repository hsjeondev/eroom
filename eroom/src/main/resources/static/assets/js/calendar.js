(function (factory) {
  typeof define === 'function' && define.amd ? define(factory) : factory();
})(function () {
  'use strict';

  /* -------------------------------------------------------------------------- */
  const camelize = str => {
    const text = str.replace(/[-_\s.]+(.)?/g, (_, c) => (c ? c.toUpperCase() : ''));
    return `${text.substr(0, 1).toLowerCase()}${text.substr(1)}`;
  };

  const getData = (el, data) => {
    try {
      return JSON.parse(el.dataset[camelize(data)]);
    } catch (e) {
      return el.dataset[camelize(data)];
    }
  };

  const renderCalendar = (el, option) => {
    const { merge } = window._;

    const options = merge(
      {
        initialView: 'dayGridMonth',
        editable: true,
        direction: document.querySelector('html').getAttribute('dir'),
        headerToolbar: {
          left: 'prev,next today',
          center: 'title',
          right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        buttonText: {
          month: 'Month',
          week: 'Week',
          day: 'Day'
        },
        events: [] // 🔥 기본 이벤트 제거
      },
      option
    );

    const calendar = new window.FullCalendar.Calendar(el, options);
    calendar.render();

    document
      .querySelector('.navbar-vertical-toggle')
      ?.addEventListener('navbar.vertical.toggle', () => calendar.updateSize());

    return calendar;
  };

  const fullCalendarInit = () => {
    const { getData } = window.phoenix.utils;
    const calendars = document.querySelectorAll('[data-calendar]');

    calendars.forEach(item => {
      const options = getData(item, 'calendar');
      renderCalendar(item, {
        ...options,
        events: [] // 🔥 항상 빈 배열로 고정
      });
    });
  };

  const fullCalendar = {
    renderCalendar,
    fullCalendarInit
  };

  const getTemplate = event => `
<div class="modal-header ps-card border-bottom">
  <div>
    <h4 class="modal-title text-1000 mb-0">${event.title}</h4>
    ${event.extendedProps.organizer ? `<p class="mb-0 fs--1 mt-1">by <a href="#">${event.extendedProps.organizer}</a></p>` : ''}
  </div>
  <button type="button" class="btn p-1 fw-bolder" data-bs-dismiss="modal" aria-label="Close">
    <span class='fas fa-times fs-0'></span>
  </button>
</div>
<div class="modal-body px-card pb-card pt-1 fs--1">
  ${event.extendedProps.description ? `
    <div class="mt-3 border-bottom pb-3">
      <h5 class='mb-0 text-800'>Description</h5>
      <p class="mb-0 mt-2">${event.extendedProps.description.split(' ').slice(0, 30).join(' ')}</p>
    </div>` : ''}
  <div class="mt-4 ${event.extendedProps.location ? 'border-bottom pb-3' : ''}">
    <h5 class='mb-0 text-800'>Date and Time</h5>
    <p class="mb-1 mt-2">
      ${window.dayjs && window.dayjs(event.start).format('dddd, MMMM D, YYYY, h:mm A')} 
      ${event.end ? `– ${window.dayjs && window.dayjs(event.end).subtract(1, 'day').format('dddd, MMMM D, YYYY, h:mm A')}` : ''}
    </p>
  </div>
  ${event.extendedProps.location ? `
    <div class="mt-4 ">
      <h5 class='mb-0 text-800'>Location</h5>
      <p class="mb-0 mt-2">${event.extendedProps.location}</p>
    </div>` : ''}
  ${event.schedules ? `
    <div class="mt-3">
      <h5 class='mb-0 text-800'>Schedule</h5>
      <ul class="list-unstyled timeline mt-2 mb-0">
        ${event.schedules.map(schedule => `<li>${schedule.title}</li>`).join('')}
      </ul>
    </div>` : ''}
</div>
<div class="modal-footer d-flex justify-content-end px-card pt-0 border-top-0">
  <a href="#" class="btn btn-phoenix-secondary btn-sm">
    <span class="fas fa-pencil-alt fs--2 mr-2"></span> Edit
  </a>
  <button class="btn btn-phoenix-danger btn-sm" data-calendar-event-remove>
    <span class="fa-solid fa-trash fs--1 mr-2" data-fa-transform="shrink-2"></span> Delete
  </button>
  <a href="#" class="btn btn-primary btn-sm">
    See more details
    <span class="fas fa-angle-right fs--2 ml-1"></span>
  </a>
</div>
`;

  const appCalendarInit = () => {
    const Selectors = {
      ADD_EVENT_FORM: '#addEventForm',
      ADD_EVENT_MODAL: '#addEventModal',
      CALENDAR: '#appCalendar',
      CALENDAR_TITLE: '.calendar-title',
      CALENDAR_DAY: '.calendar-day',
      CALENDAR_DATE: '.calendar-date',
      DATA_CALENDAR_VIEW: '[data-fc-view]',
      DATA_EVENT: '[data-event]',
      EVENT_DETAILS_MODAL: '#eventDetailsModal',
      EVENT_DETAILS_MODAL_CONTENT: '#eventDetailsModal .modal-content',
      EVENT_START_DATE: '#addEventModal [name="startDate"]',
      INPUT_TITLE: '[name="title"]'
    };

    const Events = {
      CLICK: 'click',
      SHOWN_BS_MODAL: 'shown.bs.modal',
      SUBMIT: 'submit'
    };

    const DataKeys = {
      EVENT: 'event',
      FC_VIEW: 'fc-view'
    };

    const updateDay = day => {
      const days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
      return days[day];
    };

    const setCurrentDate = () => {
      const dateObj = new Date();
      const month = dateObj.toLocaleString('en-US', { month: 'short' });
      const date = dateObj.getDate();
      const day = dateObj.getDay();
      const year = dateObj.getFullYear();
      const newdate = `${date}  ${month},  ${year}`;
      document.querySelector(Selectors.CALENDAR_DAY).textContent = updateDay(day);
      document.querySelector(Selectors.CALENDAR_DATE).textContent = newdate;
    };

    setCurrentDate();

    const updateTitle = currentData => {
      const { currentViewType } = currentData;
      if (currentViewType === 'timeGridWeek') {
        const weekStartsDate = currentData.dateProfile.currentRange.start;
        const weekEndDate = currentData.dateProfile.currentRange.end;
        const startingMonth = weekStartsDate.toLocaleString('en-US', { month: 'short' });
        const endingMonth = weekEndDate.toLocaleString('en-US', { month: 'short' });
        const startingDate = weekStartsDate.getDate();
        const endingDate = weekEndDate.getDate();
        document.querySelector(Selectors.CALENDAR_TITLE).textContent = `${startingMonth} ${startingDate} - ${endingMonth} ${endingDate}`;
      } else {
        document.querySelector(Selectors.CALENDAR_TITLE).textContent = currentData.viewTitle;
      }
    };

    const appCalendar = document.querySelector(Selectors.CALENDAR);
    const addEventForm = document.querySelector(Selectors.ADD_EVENT_FORM);
    const addEventModal = document.querySelector(Selectors.ADD_EVENT_MODAL);
    const eventDetailsModal = document.querySelector(Selectors.EVENT_DETAILS_MODAL);

    if (appCalendar) {
      const calendar = fullCalendar.renderCalendar(appCalendar, {
        headerToolbar: false,
        dayMaxEvents: 3,
        height: 800,
        stickyHeaderDates: false,
        views: {
          week: {
            eventLimit: 3
          }
        },
        eventTimeFormat: {
          hour: 'numeric',
          minute: '2-digit',
          omitZeroMinute: true,
          meridiem: true
        },
        events: [], // 🔥 항상 빈 이벤트 배열
        eventClick: info => {
          if (info.event.url) {
            window.open(info.event.url, '_blank');
            info.jsEvent.preventDefault();
          } else {
            const template = getTemplate(info.event);
            document.querySelector(Selectors.EVENT_DETAILS_MODAL_CONTENT).innerHTML = template;
            new window.bootstrap.Modal(eventDetailsModal).show();
          }
        },
        dateClick(info) {
          new window.bootstrap.Modal(addEventModal).show();
          const flatpickr = document.querySelector(Selectors.EVENT_START_DATE)._flatpickr;
          flatpickr.setDate([info.dateStr]);
        }
      });

      updateTitle(calendar.currentData);

      document.querySelectorAll(Selectors.DATA_EVENT).forEach(button => {
        button.addEventListener(Events.CLICK, e => {
          const type = getData(e.currentTarget, DataKeys.EVENT);
          switch (type) {
            case 'prev': calendar.prev(); break;
            case 'next': calendar.next(); break;
            case 'today': calendar.today(); break;
            default: calendar.today(); break;
          }
          updateTitle(calendar.currentData);
        });
      });

      document.querySelectorAll(Selectors.DATA_CALENDAR_VIEW).forEach(link => {
        link.addEventListener(Events.CLICK, e => {
          e.preventDefault();
          const view = getData(e.currentTarget, DataKeys.FC_VIEW);
          calendar.changeView(view);
          updateTitle(calendar.currentData);
        });
      });

      if (addEventForm) {
        addEventForm.addEventListener(Events.SUBMIT, e => {
          e.preventDefault();
          const { title, startDate, endDate, label, description, allDay } = e.target;
          calendar.addEvent({
            title: title.value,
            start: startDate.value,
            end: endDate.value || null,
            allDay: allDay.checked,
            className: `text-${label.value}`,
            description: description.value
          });
          e.target.reset();
          window.bootstrap.Modal.getInstance(addEventModal).hide();
        });
      }

      if (addEventModal) {
        addEventModal.addEventListener(Events.SHOWN_BS_MODAL, ({ currentTarget }) => {
          currentTarget.querySelector(Selectors.INPUT_TITLE)?.focus();
        });
      }
    }
  };

  const { docReady } = window.phoenix.utils;
  docReady(appCalendarInit);
});
