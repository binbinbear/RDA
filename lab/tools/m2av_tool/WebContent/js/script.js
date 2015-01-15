window.svlog = function f(){ log = svlog; log.history = log.history || []; log.history.push(arguments); if(this.console) { var args = arguments, newarr; args.callee = args.callee.caller; newarr = [].slice.call(args); if (typeof console.log === 'object') log.apply.call(console.log, console, newarr); else console.log.apply(console, newarr);}};
(function(a){function b(){}for(var c="assert,count,debug,dir,dirxml,error,exception,group,groupCollapsed,groupEnd,info,log,markTimeline,profile,profileEnd,time,timeEnd,trace,warn".split(","),d;!!(d=c.pop());){a[d]=a[d]||b;}})
(function(){try{console.log();return window.console;}catch(a){return (window.console={});}}());

if (typeof SNAPVOLUMES == "undefined") {
  SNAPVOLUMES = new Object();
}

SNAPVOLUMES.fade_delay = 250;
SNAPVOLUMES.config_tabs = null;
SNAPVOLUMES.volumes_tabs = null;
SNAPVOLUMES.directory_tabs = null;
SNAPVOLUMES.activity_tabs = null;
SNAPVOLUMES.infrastructure_tabs = null;

SNAPVOLUMES.features = {};

SNAPVOLUMES.ajax = {
  pool: [],

  request: function(params) {
    var xhr = $.ajax(params);
    SNAPVOLUMES.ajax.add(xhr);
    return xhr;
  },

  add: function(xhr) {
    SNAPVOLUMES.ajax.pool.push(xhr);
    return this;
  },

  rem: function(xhr) {
    if( !xhr ) return this;

    SNAPVOLUMES.ajax.pool = $.grep(SNAPVOLUMES.ajax.pool, function(x) { return x != xhr; });
    return this;
  },

  abortAll: function() {
    $.each(SNAPVOLUMES.ajax.pool, function(idx, xhr) {
      xhr.abort();
    });
  }
};

SNAPVOLUMES.main_tabs = {
  hideDisabledTabs: function(should_hide) {
    if( should_hide ) $("ul.ui-tabs-nav").find("li.ui-state-disabled").addClass("hidden");
    return this;
  },

  defaultConfig: function() {
    return {
      fx: {
        opacity: "toggle",
        duration: SNAPVOLUMES.fade_delay
      },
      cache: false,
      ajaxOptions: {
        cache: false,
        error: function( xhr, status, index, anchor ) {
          SNAPVOLUMES.error.render(anchor.hash, status);
        }
      },
      tabTemplate: "<li><a title='#{label}' href='#{href}'>#{label}</a></li>",
      panelTemplate: "<div class='ui-tabs-hide'></div>",
      selected: -1,
      collapsible: true,
      select: function(event, ui) {
        SNAPVOLUMES.ajax.abortAll();

        $(window).off('focus');

        var container = $(this),
          panel     = $(ui.panel);

        if (container.tabs("option", "selected") === ui.index) {
          SNAPVOLUMES.anchor.setIfClick(ui.tab.hash);
          panel.fadeOut(SNAPVOLUMES.fade_delay, function() {
              panel.empty();
              container.tabs('load', ui.index);
              panel.hide().fadeIn(SNAPVOLUMES.fade_delay, function() {
                panel.show();
              });
            }
          );
          return false;
        }
      },
      show: function(event, ui) {
        $("#body").find("div.ui-tabs-panel.ui-tabs-hide").empty();
      }
    }
  }
};

SNAPVOLUMES.strings = {
  license: {
    applied: 'License applied.<br/>Thanks for using App Volumes!',
    exceeded: 'License usage exceeded.<br/>Please contact App Volumes support!',
    warning: 'Usage is approaching the license limit.<br/>Please contact App Volumes support!'
  },
  appstacks: {
    not_on_page_dialog: 'AppStack details:'
  },
  ad: {
    config: {
      set: 'Active Directory configuration set.',
      updated: 'Active Directory configuration updated'
    }
  },
  administrators: {
    config: {
      required: 'Administrators group must be re-set or no one will be able to login!'
    }
  },
  hypervisor: {
    config: {
      set: 'Hypervisor configuration set.',
      updated: 'Hypervisor configuration updated.',
      changed: 'Hypervisor changed. You must reset the default storage settings.'
    }
  },
  writables: {
    update: {
      set: 'Set writable update file'
    }
  }
};

SNAPVOLUMES.reload_page_on_cancel = true;

SNAPVOLUMES.online_check = function(url) {
  if( url === window.location.href ) {
    window.location.reload();
  }
};

SNAPVOLUMES.anchor = {
  last_anchor: '',
  is_hash_change: false,

  sanitizedHash: function() {
    return window.location.hash.toString().replace('#', '');
  },

  isSameAs: function(anchor) {
    return (this.sanitizedHash() === anchor);
  },

  isSameAsLast: function() {
    return (this.sanitizedHash() === this.last_anchor);
  },

  isEmpty: function() {
    return ('' === window.location.hash);
  },

  isDefault: function(anchor) {
    if ( SNAPVOLUMES.ft && anchor.replace('#','') === 'License' ) return true;
    return ($.inArray(anchor.replace('#',''), SNAPVOLUMES.navbar.defaults) > -1);
  },

  parts: function() {
    var current_tab = this.sanitizedHash();
    var tab_parts = current_tab.split('/');

    if ('' === tab_parts[0] ) {
      tab_parts.shift();
    }

    return tab_parts;
  },

  set: function(anchor) {
    if (this.isSameAs(anchor)) return;
    if (this.isEmpty() && this.isDefault(anchor)) {
      this.last_anchor = '';
      return;
    }

    var str = '/' + anchor.replace('#', '');

    this.last_anchor = str;
    window.location.hash = str;
    this.is_hash_change = false;
  },

  go: function(anchor) {
    window.location = anchor;
  },

  setIfClick: function(anchor) {
    if( !this.is_hash_change ) {
      this.set(anchor);
    }
  },

  get: function(index) {
    var parts = this.parts();
    return parts[index];
  },

  route: function() {
    var parts = this.parts();
    var record_id = parseInt(parts[1]);

    return {
      tab: parts[0],
      record_id: record_id,
      collection_action: (record_id > 0) ? null : parts[1],
      member_action: parts[2]
    }
  },

  matches: function(index, match) {
    var parts = this.parts();
    return (match === parts[index]);
  },

  loadTab: function(tab_node, default_tab, is_hash_change) {
    var tab = this.get(0);

    this.is_hash_change = is_hash_change;

    if( is_hash_change && SNAPVOLUMES.anchor.isSameAsLast() ) {
      // this prevents the detail page from being loaded again when navigating to a member action
      // Consider #/Computers/5 -> #/Computers/5/assign - without this, the page will flash as
      // #/Computers/5 is loaded and then immediately replaced
      this.is_hash_change = false;
      return;
    }

    tab_node.find('li').removeClass('ui-state-focus');

    var tab_exists = tab_node.find('[href=#' + tab + ']').length > 0;

    if ( tab && tab_exists ) {
      tab_node.tabs('select', tab);
    } else {
      tab_node.tabs('select', default_tab);
    }
  }
};

SNAPVOLUMES.navbar = {
  defaults: ['Pending_Actions', 'AppStacks', 'Online', 'Machines', 'License'],  // status = 0, volumes = 1, directory = 2, ...

  setupActivityTab: function(default_tab, disabled_ids) {
    SNAPVOLUMES.navbar.defaults[0] = default_tab;
    SNAPVOLUMES.activity_tabs.tabs("option", "disabled", disabled_ids || []);
    SNAPVOLUMES.anchor.loadTab(SNAPVOLUMES.activity_tabs, default_tab);
  },

  setupVolumesTab: function(default_tab, disabled_ids) {
    SNAPVOLUMES.navbar.defaults[1] = default_tab;
    SNAPVOLUMES.volumes_tabs.tabs("option", "disabled", disabled_ids || []);
    SNAPVOLUMES.anchor.loadTab(SNAPVOLUMES.volumes_tabs, default_tab);
  },

  setupDirectoryTab: function(default_tab, disabled_ids) {
    SNAPVOLUMES.navbar.defaults[2] = default_tab;
    SNAPVOLUMES.directory_tabs.tabs("option", "disabled", disabled_ids || []);
    SNAPVOLUMES.anchor.loadTab(SNAPVOLUMES.directory_tabs, default_tab);
  },

  setupInfrastructureTab: function(default_tab, disabled_ids) {
    SNAPVOLUMES.navbar.defaults[3] = default_tab;
    SNAPVOLUMES.infrastructure_tabs.tabs("option", "disabled", disabled_ids || []);
    SNAPVOLUMES.anchor.loadTab(SNAPVOLUMES.infrastructure_tabs, default_tab);
  },

  setupConfigurationTab: function(default_tab) {
    SNAPVOLUMES.navbar.defaults[4] = default_tab;
    SNAPVOLUMES.anchor.loadTab(SNAPVOLUMES.config_tabs, default_tab);
  },

  loadActivityTab: function() {
    SNAPVOLUMES.anchor.loadTab(SNAPVOLUMES.activity_tabs, SNAPVOLUMES.navbar.defaults[0], true);
  },

  loadVolumesTab: function() {
    SNAPVOLUMES.anchor.loadTab(SNAPVOLUMES.volumes_tabs, SNAPVOLUMES.navbar.defaults[1], true);
  },

  loadDirectoryTab: function() {
    SNAPVOLUMES.anchor.loadTab(SNAPVOLUMES.directory_tabs, SNAPVOLUMES.navbar.defaults[2], true);
  },

  loadInfrastructureTab: function() {
    SNAPVOLUMES.anchor.loadTab(SNAPVOLUMES.infrastructure_tabs, SNAPVOLUMES.navbar.defaults[3], true);
  },

  loadConfigurationTab: function() {
    SNAPVOLUMES.anchor.loadTab(SNAPVOLUMES.config_tabs, SNAPVOLUMES.navbar.defaults[4], true);
  }
};

SNAPVOLUMES.leaderboard = {
  data_table_config: {
    "bJQueryUI": true,
    "bFilter": false,
    "bPaginate": false,
    "bLengthChange": false,
    "bInfo": false,
    "bSort": false,
    "bAutoWidth": false,
    "bSortClasses": false,
    "oLanguage": {
      "sInfoEmpty": "",
      "sEmptyTable": "",
      "sZeroRecords": '<span class="zero-record-filter">No matches</span>',
      "sLoadingRecords": "Loading..."
    }
  }
};

SNAPVOLUMES.help = {
  popInfo: function(title, body_element) {
    $("#dialog_appstack").dialog({
      title: "Information: " + title,
      buttons: [
        {
          "id": "info_ok_button",
          "text": "Ok",
          "click": function() {
            $("#dialog_appstack").dialog("close");
          }
        }
      ]
    }).html($(body_element).html()).dialog("open");
  }
};

SNAPVOLUMES.assign_controls = {
  observeLimitCheckbox: function(name) {
    var prefix = "#" + name;

    $(prefix + "_limit_mount").off().on('click', function() {
      $(prefix + "_mount_prefix_section").toggle($(prefix + "_limit_mount").is(':checked'));
    });

    return this;
  },

  observeLimitAttachmentInfo: function(name) {
    var prefix = "#" + name;

    $(prefix + "_mount_prefix_info").off().on('click', function() {
      SNAPVOLUMES.help.popInfo("Limit Attachment", prefix + "_mount_prefix_help");
    });

    return this;
  }
};

SNAPVOLUMES.jobs = {
  pending: 0,
  buttoned: false,
  timer_handle: null,

  init: function() {
    SNAPVOLUMES.jobs.checkPending();
  },

  setupButton: function() {
    if( this.buttoned ) return this;

    this.buttoned = true;

    $('#pending_jobs_button').button({icons: { primary: "ui-icon-clock" }}).off('click').on('click', function() {
      location.href = '/activity?fresh=' + Date.now() + '#Pending_Actions';
    });

    return this;
  },

  getBadge: function() {
    return $('#pending_jobs_button').find('span.ui-button-icon-primary');
  },

  checkPending: function(wait, timeout) {
    wait = wait || 10000;
    if( isNaN(timeout) ) timeout = 30;

    var badge = SNAPVOLUMES.jobs.setupButton().getBadge();

    $.ajax({
      "dataType": 'json',
      "url": '/cv_api/jobs/pending',
      "cache": false,
      "global": false,
      "success": function(data) {
        if( SNAPVOLUMES.jobs.setCount(data.pending) > 0 && timeout > 0) {
          SNAPVOLUMES.jobs.showSpinner(badge);
          setTimeout(function() { SNAPVOLUMES.jobs.checkPending(wait, --timeout); }, wait);
        } else {
          SNAPVOLUMES.jobs.showStopped(badge);
        }

        $.event.trigger("pending_count_update", data);
      },
      "error": function(message) {
        SNAPVOLUMES.jobs.showAlert(badge);
      }
    });
  },

  stopCheck: function() {
    if( SNAPVOLUMES.jobs.timer_handle ) {
      clearTimeout(SNAPVOLUMES.jobs.timer_handle);
    }
  },

  setCount: function(cnt) {
    SNAPVOLUMES.jobs.pending = parseInt(cnt) || 0;
    $('#pending_jobs_count').text(SNAPVOLUMES.jobs.pending);
    return SNAPVOLUMES.jobs.pending;
  },

  showSpinner: function(badge) {
    $("#pending_jobs").show();
    badge.addClass('spinner-jobs');
    return this;
  },

  showAlert: function(badge) {
    $("#pending_jobs").show();
    badge.removeClass('spinner-jobs').removeClass('ui-icon-clock').addClass('ui-icon-alert');
  },

  showStopped: function(badge) {
    badge.removeClass('spinner-jobs').removeClass('ui-icon-alert').addClass('ui-icon-clock');
  }
};

SNAPVOLUMES.flash = {
  last_msg: "",
  message: function(msg, type) {
    if (msg === undefined || msg === null || msg == "" || msg == SNAPVOLUMES.flash.last_msg) return;
    //SNAPVOLUMES.flash.last_msg = msg;

    var options = {
      header: '<span class="ui-icon ui-icon-info left"></span>',
      theme: 'ui-state-default',
      life: 5000,
      close: function() {
        SNAPVOLUMES.flash.last_msg = "";
      }
    };

    switch (type) {
      case "error":
        options.header = '<span class="ui-icon ui-icon-alert left"></span>';
        options.theme = 'ui-state-error';
        options.sticky = true;
        break;
      case "warning":
        options.header = '<span class="ui-icon ui-icon-alert left"></span>';
        options.theme = 'ui-state-highlight';
        options.life = 60000;
        break;
      case "success":
        options.header = '<span class="ui-icon ui-icon-check left"></span>';
        break;
      default:
        break;
    }

    $('#jgrowl_msg').jGrowl(msg, options);
  },

  success: function(msg) {
    this.message(msg, 'success');
  },

  error: function(msg) {
    this.message(msg, 'error');
  },

  warning: function(msg) {
    this.message(msg, 'warning');
  },

  close: function() {
    $("div.jGrowl").jGrowl("close");
  },

  jsonResponse: function(json) {
    if (json) {
      if (json.success) SNAPVOLUMES.flash.success(json.success);
      if (json.warning) SNAPVOLUMES.flash.warning(json.warning);
      if (json.error) SNAPVOLUMES.flash.error(json.error);

      $.map(json.successes || [], function(msg) { SNAPVOLUMES.flash.success(msg); });
      $.map(json.warnings || [], function(msg) { SNAPVOLUMES.flash.warning(msg); });
      $.map(json.errors || [], function(msg) { SNAPVOLUMES.flash.error(msg); });
    }
  }
};

SNAPVOLUMES.data_tables = {
  resetTable: function(data_table) {
    if (data_table) {
      data_table.fnClearTable(true);
      data_table.fnDestroy();
      data_table = null;
    }
  },

  showChecked: function(selector, cnt) {
    var info = cnt > 0 ? $('<span class="ui-state-highlight"></span>').append(' with ' + cnt + ' selected') : '';
    $(selector).empty().append(info);
  },

  selectedInfo: function(select_info_id) {
    return (select_info_id ? (' <span id="' + select_info_id + '"></span>') : '');
  },

  infoResultLine: function(suffix, select_info_id) {
    return 'Showing <i>_START_</i> to <i>_END_</i> of <i>_TOTAL_</i> ' + suffix + SNAPVOLUMES.data_tables.selectedInfo(select_info_id);
  },

  filteredCheckboxes: function(data_table) {
    return data_table.$("input[type=checkbox]", {"filter": "applied"});
  },

  checkedRows: function(data_table) {
    if (!data_table) return null;
    return data_table._(data_table.$('input:checked').parents('tr'));
  },

  checkedCount: function(data_table) {
    if( !data_table ) return 0;

    return data_table.$('input:checked').length;
  },

  toggleRange: function(checkboxes, beg_num, end_num, checked) {
    if( parseInt(beg_num) >= 0 ) {
      var selection = checkboxes.slice(Math.min(beg_num, end_num), Math.max(beg_num, end_num)+1);

      if( checked ) {
        SNAPVOLUMES.data_tables.checkSelection(selection);
      } else {
        SNAPVOLUMES.data_tables.uncheckSelection(selection);
      }
    }
  },

  handleRangeSelection: function(event, checkbox, data_table, last_click_index) {
    var boxes = SNAPVOLUMES.data_tables.filteredCheckboxes(data_table);
    var end_index = boxes.index(checkbox);

    if( event.shiftKey ) {
      SNAPVOLUMES.data_tables.toggleRange(boxes, last_click_index, end_index, $(checkbox).is(":checked"));
    }

    return end_index;
  },

  checkSelection: function(selection) {
    selection.prop("checked", true).parents("tr").addClass("ui-state-highlight").addClass('checked');
  },

  uncheckSelection: function(selection) {
    selection.prop("checked", false).parents("tr").removeClass("ui-state-highlight").removeClass('checked');
  },

  checkRows: function(data_table, selector) {
    SNAPVOLUMES.data_tables.checkSelection( data_table.$(selector, {"filter": "applied"}) );
  },

  uncheckRows: function(data_table, selector) {
    SNAPVOLUMES.data_tables.uncheckSelection( data_table.$(selector, {"filter": "applied"}) );
  },

  toggleCheckbox: function(data_table, checkbox, check_all_classname) {
    var all = $(checkbox).hasClass(check_all_classname);

    if ($(checkbox).is(":checked")) {
      SNAPVOLUMES.data_tables.checkRows(data_table, all ? 'input[type=checkbox]:not(:checked)' : checkbox);
    } else {
      SNAPVOLUMES.data_tables.uncheckRows(data_table, all ? 'input[type=checkbox]:checked' : checkbox);
      $('.dataTable input.' + check_all_classname).prop('checked', false);
    }
  },

  toggleRadio: function(data_table, radio) {
    if ($(radio).is(":checked")) {
      SNAPVOLUMES.data_tables.uncheckRows(data_table, 'input[type=radio]');
      SNAPVOLUMES.data_tables.checkRows(data_table, radio);
    } else {
      SNAPVOLUMES.data_tables.uncheckRows(data_table, radio);
    }
  },

  disableFilter: function(table) {
    $(table).parent().find(".dataTables_filter").find("input:text").prop("disabled", true);
  },

  setupFilter: function(table, new_id) {
    var filter = $(table).parent().find(".dataTables_filter").find("input:text");

    filter.prop("disabled", false);
    filter.attr('id', new_id || $(table).attr("id") + "_filter_input");
    filter.off("dblclick.select-all").on("dblclick.select-all", function() {
        $(this).select();
      }
    ).off('keyup.highlight').on('keyup.highlight',
      function() {
        filter.removeClass('ui-state-highlight');
      }
    );

    if( filter.val() ) {
      filter.select().addClass('ui-state-highlight');
    }

    var zero_record = $(table).find('.zero-record-filter');

    zero_record.
      text('No records matching previously applied filter: "').
      append($("<strong></strong>").text(filter.val())).
      append('" ').
      append($("<span></span>").css({color: "gray"}).text("  (press delete to clear)"));

    return this;
  },

  storageKey: function(table_id) {
    return 'CvoTable_' + table_id
  },

  addRefreshButton: function(table, callback) {
    if( !localStorage || !callback) return null;

    var table_id = $(table).attr("id");
    if( !table_id ) return null;

    var key = SNAPVOLUMES.data_tables.storageKey(table_id);
    var disabled = localStorage.getItem(key) ? '' : ' ui-state-disabled';

    return $('<div>').
      addClass("table-refresh ui-icon ui-icon-arrowreturnthick-1-w right" + disabled).
      attr("id", table_id + "_refresh").
      attr("title", "Reset table defaults").
      on("click",function() {
        SNAPVOLUMES.data_tables.resetTable($(table).dataTable());
        localStorage.removeItem(key);
        callback();
      }).appendTo("#" + table_id + "_length");
  },

  saveState: function(oSettings, oData) {
    if (oSettings && oSettings.sTableId && localStorage) {
      $("#" + oSettings.sTableId + "_refresh").removeClass("ui-state-disabled");
      localStorage.setItem(SNAPVOLUMES.data_tables.storageKey(oSettings.sTableId), JSON.stringify(oData));
    }
  },

  loadState: function(oSettings) {
    if (oSettings && oSettings.sTableId && localStorage) {
      return JSON.parse(localStorage.getItem(SNAPVOLUMES.data_tables.storageKey(oSettings.sTableId)));
    }
  },

  jumpTop: function(table) {
    var wrapper = $(table).parent();
    wrapper.find(".fg-button").off("click.jump-top").on("click.jump-top", function() {
      var offset = wrapper.offset();
      $(window).scrollTop(offset ? offset.top : 0);
    });

    return this;
   },

  defaultLanguageConfig: function(opts) {
    opts = opts || {};

    return $.extend({
      "sInfoEmpty": "",
      "sSearch": "Filter",
      "sLengthMenu": "Show _MENU_",
      "sEmptyTable": "No records found",
      "sZeroRecords": '<span class="zero-record-filter">No matches</span>',
      "sLoadingRecords": '<div class="spinner">Loading...</div>'
    }, opts);
  },

  defaultConfig: function(record_type) {
    return {
      "bJQueryUI": true,
      "bFilter": false,
      "bPaginate": false,
      "bLengthChange": false,
      "bSortClasses": false,
      "bInfo": false,
      "oLanguage": {
        "sSearch": 'Filter',
        "sEmptyTable": "No " + record_type,
        "sZeroRecords": '<span class="zero-record-filter">No matches</span>'
      }
    }
  },

  defaultConfigStatic: function(record_type) {
    var cfg = SNAPVOLUMES.data_tables.defaultConfig(record_type);
    cfg["bSort"] = false;
    return cfg;
  },

  appstackStatus: function(value, action) {
    switch(action) {
      case "sort":
        switch(value) {
          case "creating":      return -1;
          case "unprovisioned": return 0;
          case "provisioning":  return 1;
          case "enabled":       return 2;
          case "assigned":      return 3;
          case "Not Licensed":  return 4;
          case "canceled":      return 5;
          case "missing":       return 6;
          case "disabled":      return 7;
          case "reserved":      return 8;
          case "unreachable":   return 9;
          default:              return 10;
        }

      case "display":
        var classname = '';

        switch (value) {
          case 'provisioning':  classname = 'status-provisioning';  break;
          case 'unprovisioned': classname = 'status-unprovisioned'; break;
          case 'creating':      classname = 'status-unprovisioned'; break;
          case 'unreachable':   classname = 'status-disabled';      break;
          case 'reserved':      classname = 'status-disabled';      break;
          case 'disabled':      classname = 'status-disabled';      break;
          case 'missing':       classname = 'status-disabled';      break;
          case 'canceled':      classname = 'status-disabled';      break;
          case 'assigned':      classname = 'status-provisioning';  break;
          case 'failed':        classname = 'status-failed';        break;
          case 'Not Licensed':  classname = 'status-disabled';      break;
        }

        return '<div class="capitalized no-select ' + classname + '">' + value + '</div>';

      default:
        return value;
    }
  }
};

SNAPVOLUMES.table_row = {
  indicatorIcon: function(nTr) {
    return $($(nTr).find('span.ui-icon.expando')[0]);
  },

  showOpenedIcon: function(nTr) {
    this.indicatorIcon(nTr).removeClass('ui-icon-circle-plus').addClass('ui-icon-circle-minus');
    return this;
  },

  showLoadingIcon: function(nTr) {
    this.indicatorIcon(nTr).removeClass('ui-icon-circle-plus').removeClass('ui-icon-circle-minus').addClass('ui-icon-clock');
    return this;
  },

  showClosedIcon: function(nTr) {
    this.indicatorIcon(nTr).removeClass('ui-icon-circle-minus').addClass('ui-icon-circle-plus');
    return this;
  },

  isOpen: function(nDt, nTr) {
    if( !nDt || !nTr ) return false;

    return nDt.fnIsOpen(nTr);
  },

  closeRow: function(nDt, nTr) {
    if( !nDt || !nTr ) return;

    this.showClosedIcon(nTr);
    $(nTr).removeClass('row-open');

    return nDt.fnClose(nTr);
  },

  openRow: function(nDt, nTr, contents) {
    if( !nDt || !nTr ) return;

    this.showOpenedIcon(nTr);
    $(nTr).addClass('row-open');

    return nDt.fnOpen(nTr, contents, 'details');
  }
};

SNAPVOLUMES.error = {
  render: function(container, message) {
    if( message === 'abort' ) return;

    $(container).html(
      $('<div style="text-align: center; font-size: 16px; color: red; margin-top: 20px"></div>').append(
        $('<div style="margin: 0 auto; padding-bottom: 10px" class="sv-ui-icon-lrg sv-ui-icon-warn-c"></div>')
      ).append(
        $('<span></span>').text(message || "Internal Server Error")
      )
    );
  },

  failedLoad: function(container, message) {
    $(container).html(
      $('<div style="text-align: center; font-size: 16px; color: gray; margin-top: 20px"></div>').append(
          $('<div style="margin: 0 auto; padding-bottom: 10px" class="sv-ui-icon-lrg sv-ui-icon-warn-gray-c"></div>')
        ).append(
          $('<span></span>').text(message || "Canceled")
        )
    );

    return this;
  }
};

SNAPVOLUMES.confirm = {
  term: function(item_count, single_term, plural_term) {
    single_term = single_term || "entity";
    plural_term = plural_term || "entities";

    return (item_count > 1) ? ('<em>' + item_count + '</em> ' + plural_term) : single_term;
  },

  generateQuestion: function(headline, items) {
    var node = $('<p class="dialog_confirm_text"></p>').html(headline);

    if( items ) {
      var list = $('<div class="dialog_confirm_list ui-corner-all"></div>');

      $.map($.makeArray(items), function(i) {
        list.append($('<div></div>').html('&bull; ' + i));
      });

      node.append(list.append('<div><br/></div>'));
    }

    return node;
  },

  addListNote: function(dialog, note) {
    return dialog.append(note);
  },

  addChoice: function(dialog, delayed_text, immed_text, immed_warning, immed_disabled) {
    var choice = $('<div style="width: 350px; float:left;"></div>');
    var check_immed = true;

    if( delayed_text ) {
      check_immed = false;

      choice.append(
        $('<input type="radio" id="confirm_check_default" name="confirm_check">').prop('checked', true),
        $('<label for="confirm_check_default"></label>').html(delayed_text)
      );
    }

    if( immed_text ) {
      var input_radio = $('<input type="radio" id="chkreal" name="confirm_check">');
      var input_label = $('<label for="chkreal"></label>');
      if (immed_disabled) {
        input_radio.prop('disabled', true);
        input_label.css('color', 'gray').html(immed_text+" (disabled)");
      } else {
        input_radio.prop('checked', check_immed);
        input_label.html(immed_text);
      }
      choice.append($('<br>'), input_radio, input_label);
    }

    dialog.siblings('.ui-dialog-buttonpane').prepend(choice);

    SNAPVOLUMES.confirm.setWarning(dialog, immed_warning);

    $("#chkreal_warn").toggle($("#chkreal").is(":checked"));

    $("#chkreal, #confirm_check_default").off("click").on("click", function() {
      $("#chkreal_warn").toggle($("#chkreal").is(":checked"));
    });
  },

  setWarning: function(dialog, warning_text) {
    var warn_div = $('<div id="chkreal_warn" style="clear:both; padding-top: 6px;"></div>').append(
      $('<div class="ui-icon ui-icon-alert left" style="margin-right: 2px;"></div>'),
      $('<div style="color:#E39300; font-weight:bold; margin-left: 22px"></div>').append(warning_text)
    );

    dialog.siblings('.ui-dialog-buttonpane').append(warn_div);

    return warn_div;
  }
};

$(document).ajaxComplete( function(event, request) {
  SNAPVOLUMES.ajax.rem(request);

  if (request && request.status == 403) {
    window.location = '/login';
  }
});

$(document).ajaxError( function(event, jqxhr, settings, exception) {
  svlog("Request Error: ", exception, jqxhr, event, settings);

  if (settings.url === '/cv_api/license/set') return true;
  if (settings.url === '/cv_api/writables/update_files') return true;
  if (!jqxhr) return true;

  if ( jqxhr.status == 504 ) {
    SNAPVOLUMES.flash.error("Server Timeout (504)<br/>The server took too long to process the request.<br/>" + exception);
  } else if ( jqxhr.status == 503 ) {
    SNAPVOLUMES.flash.error("Server Unavailable (503)<br/>The server did not respond.<br/>" + exception);
  } else if ( jqxhr.status == 502 ) {
    SNAPVOLUMES.flash.error("Server Unavailable (502)<br/>The server did not respond.<br/>" + exception);
  } else if ( jqxhr.status == 500 && jqxhr.responseText ) {
    SNAPVOLUMES.flash.error("Server Error:<br/>" + jqxhr.responseText);
  } else if ( jqxhr.status == 500 ) {
    SNAPVOLUMES.flash.error("Server Error (500)<br/>The server failed to process the request.<br/>" + exception);
  } else if ( jqxhr.status > 500 ) {
    SNAPVOLUMES.flash.error("Server Error: <br/>" + jqxhr.responseText + '<br/>' + exception);
  } else if ( jqxhr.status == 400 && jqxhr.responseText ) {
    SNAPVOLUMES.flash.error("Request Error:<br/>" + jqxhr.responseText);
  } else if ( jqxhr.status == 400 ) {
    SNAPVOLUMES.flash.error("Bad Request (400)<br/>The request was invalid.<br/>" + exception);
  } else if ( jqxhr.status > 400 ) {
    SNAPVOLUMES.flash.error("Client Error: <br/>" + jqxhr.responseText + '<br/>' + exception);
  } else if ( jqxhr.status == 200 && jqxhr.responseText.indexOf("waiting_for_server_start") > 0 ) {
    SNAPVOLUMES.flash.error("Invalid Response<br/>The server is restarting, try again in a few moments");
  } else if( exception ) {
    if( exception !== 'abort' ) {
      SNAPVOLUMES.flash.error("Javascript Error: <br/>" + exception);
    }
  } else if ( jqxhr.status == 0 ) {
    svlog("Checking network connection...");
    setTimeout(function() { SNAPVOLUMES.online_check(window.location.href) }, 5000);
  }
});

$.widget( "custom.catcomplete", $.ui.autocomplete, {
  _renderItem: function( ul, item ) {
    return $( "<li></li>" )
      .data( "item.autocomplete", "sv-ui-icon sv-ui-icon-" + item.entity_type + " left'></span>" + item.entity_name + "</a>" )
      .appendTo( ul );
  }  
});

SNAPVOLUMES.dates = {
  filters: function() {
    "use strict";
    var f_today = new Date();
    var f_week = new Date();
    var f_month = new Date();
    f_today.setHours(0, 0, 0, 0);
    f_week.setHours(0, 0, 0, 0);
    f_month.setHours(0, 0, 0, 0);

    f_week.setDate(f_week.getDate() - 7);
    f_month.setMonth(f_month.getMonth() - 1);

    return new Array(0, f_today, f_week, f_month);
  },
  convert: function(d) {
    // Source: http://stackoverflow.com/questions/497790
    // Converts the date in d to a date-object. The input can be:
    //   a date object: returned without modification
    //  an array    : Interpreted as [year,month,day]. NOTE: month is 0-11.
    //   a number    : Interpreted as number of milliseconds
    //          since 1 Jan 1970 (a timestamp)
    //   a string    : Any format supported by the javascript engine, like
    //          "YYYY/MM/DD", "MM/DD/YYYY", "Jan 31 2009" etc.
    //  an object    : Interpreted as an object with year, month and date
    //          attributes.   **NOTE** month is 0-11.
    return (
      d.constructor === Date ? d :
        d.constructor === Array ? new Date(d[0],d[1],d[2]) :
          d.constructor === Number ? new Date(d) :
            d.constructor === String ? new Date(d) :
              typeof d === "object" ? new Date(d.year, d.month, d.date) :
                NaN
      );
  },
  compare:function(a, b) {
    // Source: http://stackoverflow.com/questions/497790
    // Compare two dates (could be of any type supported by the convert
    // function above) and returns:
    //  -1 : if a < b
    //   0 : if a = b
    //   1 : if a > b
    // NaN : if a or b is an illegal date
    // NOTE: The code inside isFinite does an assignment (=).
    return (
      isFinite(a=this.convert(a).valueOf()) &&
      isFinite(b=this.convert(b).valueOf()) ?
        (a>b)-(a<b) :
        NaN
      );
  },
  inRange:function(d, start, end) {
    // Source: http://stackoverflow.com/questions/497790
    // Checks if date in d is between dates in start and end.
    // Returns a boolean or NaN:
    //    true  : if d is between start and end (inclusive)
    //    false : if d is before start or after end
    //    NaN  : if one or more of the dates is illegal.
    // NOTE: The code inside isFinite does an assignment (=).
    return (
      isFinite(d=this.convert(d).valueOf()) &&
      isFinite(start=this.convert(start).valueOf()) &&
      isFinite(end=this.convert(end).valueOf()) ?
        start <= d && d <= end :
        NaN
      );
  }
};

$.validator.setDefaults({
  highlight: function(input) {
    $(input).addClass("ui-state-highlight");
  },
  unhighlight: function(input) {
    $(input).removeClass("ui-state-highlight");
  }
});

$.validator.addMethod('IP4Checker', function(value) {
  var ip = /^(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])(\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])){3}$/;
  return value.match(ip);
}, 'Invalid IP address');

$.validator.addMethod('html_entities', function(value, element) {
    var re = new RegExp("^[^<>\'\"&]+$");
    return this.optional(element) || re.test(value);
  }, 'Invalid characters are &amp; &quot; &apos; &lt; &gt;'
);

// datatables ajax reload call
if ($.fn.dataTableExt) {
 $.fn.dataTableExt.oApi.fnReloadAjax = function ( oSettings, sNewSource, fnCallback, bStandingRedraw ) {
    if ( typeof sNewSource != 'undefined' && sNewSource != null )
    {
        oSettings.sAjaxSource = sNewSource;
    }
    this.oApi._fnProcessingDisplay( oSettings, true );
    var that = this;
    var iStart = oSettings._iDisplayStart;
    var aData = [];
  
    this.oApi._fnServerParams( oSettings, aData );

    oSettings.fnServerData( oSettings.sAjaxSource, aData, function(json) {
        /* Clear the old information from the table */
        that.oApi._fnClearTable( oSettings );
          
        /* Got the data - add it to the table */
        var aData =  (oSettings.sAjaxDataProp !== "") ?
            that.oApi._fnGetObjectDataFn( oSettings.sAjaxDataProp )( json ) : json;
          
        for ( var i=0 ; i<aData.length ; i++ )
        {
            that.oApi._fnAddData( oSettings, aData[i] );
        }
          
        oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
        that.fnDraw();
          
        if ( typeof bStandingRedraw != 'undefined' && bStandingRedraw === true )
        {
            oSettings._iDisplayStart = iStart;
            that.fnDraw( false );
        }
          
        that.oApi._fnProcessingDisplay( oSettings, false );
          
        /* Callback user function - for event handlers etc */
        if ( typeof fnCallback == 'function' && fnCallback != null )
        {
            fnCallback( oSettings );
        }
    }, oSettings );
 };
}

SNAPVOLUMES.progress_bar = {
  show: function(title) {
    $("#progress_msg").dialog({
      title: title,
      autoOpen: true,
      dialogClass: 'ui-modal-progress',
      resizable: false,
      show: "clip",
      height: 56,
      width: 600,
      modal: true,
      closeOnEscape: false,
      open: function() {
        $(".ui-dialog-titlebar-close").hide();
      }
    });

    $('<div class="progress"></div>').appendTo("#progress_msg").progressbar().removeClass('ui-corner-all');
    return this;
  },

  hide: function() {
    $("#dialog_appstack").dialog("close");  // FIXME: Dialog creator should close this
    $("#progress_msg").dialog("close");
    return this;
  },

  change: function(title) {
    $("#progress_msg").dialog("option", "title", title);
    return this;
  },

  visible: function() {
    $("#progress_msg").is(":visible")
  }
};

SNAPVOLUMES.assignments = {};

SNAPVOLUMES.assignments.list = {
  dtable: null,
  open_count: 0,

  renderTable: function(element, url) {
    SNAPVOLUMES.assignments.list.dtable = $(element).dataTable({
      "oLanguage": SNAPVOLUMES.data_tables.defaultLanguageConfig({
        "sEmptyTable": "No assignments",
        "sInfo": SNAPVOLUMES.data_tables.infoResultLine('assignments')
      }),
      "bJQueryUI": true,
      "bAutoWidth": false,
      "bSortClasses": false,
      "bDeferRender": true,
      "sPaginationType": "full_numbers",
      "bStateSave": true,
      "fnStateSave": SNAPVOLUMES.data_tables.saveState,
      "fnStateLoad": SNAPVOLUMES.data_tables.loadState,
      "aaSorting": [
        [3, "desc"]
      ],
      "aLengthMenu": [10, 25, 50, 100, 500, 1000],
      "iDisplayLength": 10,
      "bServerSide": false,
      "sAjaxSource": url,
      "sAjaxDataProp": "assignments",
      "fnServerData": function(sSource, aoData, fnCallback) {
        SNAPVOLUMES.ajax.request({
          "dataType": 'json',
          "url": sSource,
          "data": aoData,
          "cache": false,
          "success": function(data) {
            fnCallback(data);
          },
          "complete": function(response) {
            $(".dataTables_empty").text("No volume assignments or attachments found");

            if (response && response.status != 200) {
              SNAPVOLUMES.flash.error(response.responseText);
            }
          }
        });
      },
      "aoColumns": [
        {
          "sWidth": "36px",
          "mData": "entityt",
          "mRender": function(value, action, row) {
            if ('display' !== action) return value;

            var entityt = value ? value.toLowerCase() : 'warn-d';

            return '<span title="' + value + '" class="sv-ui-icon sv-ui-icon-' + entityt + '"></span>';
          }
        },
        {
          "sWidth": "255px",
          "mData": "entity",
          "mRender": function(value, action, row) {
            if ('display' !== action) return value;

            return '<div class="break" style="width: 245px">' + value + '</div>';
          }
        },
        {
          "sWidth": "330px",
          "mData": "snapvol",
          "mRender": function(value, action, row) {
            if ('display' !== action) return value;

            return '<div class="break" style="width: 320px">' + value + '</div>';
          }
        },
        {
          "sWidth": "65px",
          "mData": "mount_prefix",
          "mRender": function(value, action, row) {
            if ('display' !== action) return value;

            if ( value ) {
              return '<span class="quick-help" title="Attaches when computer name starts with: ' + value + '">Limited</span>';
            } else {
              return '<span class="no-select" title="Attaches to any computer">Always</span>';
            }
          }
        },
        {
          "sWidth": "139px",
          "mData": "event_time",
          "mRender": function(value, action, row) {
            if ('display' === action || 'filter' === action) return row.event_time_human;
            else return value;
          }
        }
      ],

      "fnInitComplete": function(data) {
        SNAPVOLUMES.data_tables.jumpTop(this).setupFilter(this);
        SNAPVOLUMES.data_tables.addRefreshButton(this, function() {
            SNAPVOLUMES.assignments.list.renderTable(element, url);
          }
        );
      }
    });
  }
};

SNAPVOLUMES.assignments.assign_confirm = {
  openDialog: function(dialog, question, confirm_list) {
    dialog.html(SNAPVOLUMES.confirm.generateQuestion(question, confirm_list));

    SNAPVOLUMES.assignments.assign_confirm.appendChoices(dialog);

    dialog.dialog('open');
  },

  appendChoices: function(dialog) {
    SNAPVOLUMES.confirm.addChoice(
      dialog,
      'Attach AppStacks on next login or reboot',
      'Attach AppStacks immediately',
      'Users must be logged into a VM to have AppStacks attached immediately.',
      !SNAPVOLUMES.features.realtimeAttach //can not real-time attach
    );
  }
};

SNAPVOLUMES.assignments.unassign_confirm = {
  openDialog: function(dialog, question, confirm_list) {
    dialog.html(SNAPVOLUMES.confirm.generateQuestion(question, confirm_list));

    SNAPVOLUMES.assignments.unassign_confirm.appendChoices(dialog);

    dialog.dialog('open');
  },

  appendChoices: function(dialog) {
    SNAPVOLUMES.confirm.addChoice(
      dialog,
      'Detach AppStack on next logout or reboot',
      'Detach AppStack immediately',
      'Detaching an AppStack while it is in use is not recommended.<br/>Please ensure the AppStack is no longer in use.',
      !SNAPVOLUMES.features.realtimeAttach //can not real-time attach
    );
  }
};

SNAPVOLUMES.assign_one = {
  dtable: null,
  prefix: '',

  id: function(suffix) {
    return '#' + SNAPVOLUMES.assign_one.prefix + '_' + suffix;
  },

  render: function(url, element_prefix) {
    SNAPVOLUMES.assign_one.prefix = element_prefix;
    SNAPVOLUMES.assign_one.resetTable();
    SNAPVOLUMES.assign_one.setupTable(url);
  },

  resetTable: function() {
    if (SNAPVOLUMES.assign_one.dtable) {
      SNAPVOLUMES.assign_one.dtable.fnClearTable(true);
      SNAPVOLUMES.assign_one.dtable.fnDestroy();
      SNAPVOLUMES.assign_one.dtable = null;
    }
  },

  columns: function() {
    return [
      {
        "mData": "name",
        "sWidth": "491px",
        "bSortable": true,
        "bSearchable": true,
        "mRender": function(value, action, row) {
          if ('display' !== action) return value;

          return '<div class="break" style="width: 481px">' + value + '</div>';
        }
      },
      {
        "mData": "datastore_name",
        "sWidth": "220px",
        "bSortable": true,
        "bSearchable": true,
        "mRender": function(value, action, row) {
          if ('display' !== action) return value;

          return '<div class="break" style="width: 210px">' + value + '</div>';
        }
      },
      {
        "mData": "status",
        "bSortable": true,
        "bSearchable": true,
        "sWidth": "100px",
        "mRender": function(value, action, row) {
          return SNAPVOLUMES.data_tables.appstackStatus(value, action);
        }
      },
      {
        "mData": "status",
        "sWidth": "26px",
        "bSearchable": false,
        "bSortable": false,
        "mRender": function(value, action, row) {
          if( 'display' !== action ) return value;

          if ('enabled' === value) {
            return '<input type="checkbox"/>';
          } else {
            var icon = value === 'assigned' ? 'ui-icon-link' : 'ui-icon-cancel';
            return '<em class="ui-icon ' + icon + '" title="' + value + '">&nbsp;</em>';
          }
        }
      }
    ]
  },

  setupTable: function(url) {
    SNAPVOLUMES.assign_one.dtable = $(SNAPVOLUMES.assign_one.id('table')).dataTable({
      "sAjaxSource": url,
      "sAjaxDataProp": "appstacks",
      "bProcessing": false,
      "bServerSide": false,
      "bAutoWidth": false,
      "bSortClasses": false,
      "bJQueryUI": true,
      "aLengthMenu": [10, 25, 50, 100, 500, 1000],
      "iDisplayLength": 10,
      "sPaginationType": "full_numbers",
      "aaSorting": [
        [2, "asc"],
        [1, "asc"]
      ],
      "oLanguage": SNAPVOLUMES.data_tables.defaultLanguageConfig({
        "sEmptyTable": "No AppStacks",
        "sInfo": SNAPVOLUMES.data_tables.infoResultLine('AppStacks', SNAPVOLUMES.assign_one.prefix + '_select_info')
      }),
      "aoColumns": SNAPVOLUMES.assign_one.columns(),

      "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
        var checkbox = $("input[type=checkbox]", nRow);
        SNAPVOLUMES.assign_one.observe_checkbox(checkbox);
      },

      "fnInitComplete": function() {
        SNAPVOLUMES.assign_one.observe_checkbox(SNAPVOLUMES.assign_one.id('check_all'));
        SNAPVOLUMES.data_tables.jumpTop(this).setupFilter(this);
      },

      fnFooterCallback: function(nFoot, aData, iStart, iEnd, aiDisplay) {
        setTimeout(function() {
          SNAPVOLUMES.assign_one.toggle_check_actions();
        }, 5);
      }
    });
  },

  observe_checkbox: function(checkbox) {
    $(checkbox).off("change").on("change", function() {
      SNAPVOLUMES.data_tables.toggleCheckbox(SNAPVOLUMES.assign_one.dtable, this, 'check-all');
      SNAPVOLUMES.assign_one.toggle_check_actions();
    });
  },

  toggle_check_actions: function() {
    var check_count = SNAPVOLUMES.data_tables.checkedCount(SNAPVOLUMES.assign_one.dtable);
    SNAPVOLUMES.data_tables.showChecked(SNAPVOLUMES.assign_one.id('select_info'), check_count);

    if( check_count ) {
      $(SNAPVOLUMES.assign_one.id('submit')).button("enable");
    } else {
      $(SNAPVOLUMES.assign_one.id('submit')).button("disable")
    }
  },

  checked_rows: function() {
    return SNAPVOLUMES.data_tables.checkedRows(SNAPVOLUMES.assign_one.dtable);
  }
};


SNAPVOLUMES.assignment_list = {
  dtable: null,
  prefix: '',

  id: function(suffix) {
    return '#' + SNAPVOLUMES.assignment_list.prefix + '_' + suffix;
  },

  render: function(url, element_prefix) {
    SNAPVOLUMES.assignment_list.prefix = element_prefix;
    SNAPVOLUMES.assignment_list.resetTable();
    SNAPVOLUMES.assignment_list.setupTable(url);
  },

  resetTable: function() {
    SNAPVOLUMES.data_tables.resetTable(SNAPVOLUMES.assignment_list.dtable);
  },

  columns: function() {
    return [
      {
        "mData": "snapvol",
        "sWidth": "280px",
        "bSortable": false,
        "bSearchable": true,
        "mRender": function(value, action, row) {
          if ('display' !== action) return value;

          return '<div class="break" style="width: 270px">' + value + '</div>';
        }
      },
      {
        "mData": "source",
        "sWidth": "210px",
        "bSortable": false,
        "bSearchable": true,
        "mRender": function(value, action, row) {
          if ('display' !== action) return value;

          return '<div class="break" style="width: 200px">' + value + '</div>';
        }
      },
      {
        "mData": "status",
        "bSortable": false,
        "bSearchable": true,
        "sWidth": "65px",
        "mRender": function(value, action, row) {
          return SNAPVOLUMES.data_tables.appstackStatus(value, action);
        }
      },
      {
        "mData": "prefix",
        "sWidth": "65px",
        "bSortable": false,
        "bSearchable": false,
        "mRender": function(value, action, row) {
          if ('display' !== action) return value;

          if ( value ) {
            return '<span class="quick-help" title="Attaches when computer name starts with: ' + value + '">Limited</span>';
          } else {
            return '<span class="no-select" title="Attaches to any computer">Always</span>';
          }
        }
      },
      {
        "mData": "created_at",
        "sWidth": "139px",
        "bSortable": false,
        "bSearchable": false,
        "mRender": function(value, action, row) {
          if ('display' === action || 'filter' === action ) return row.created_at_human;
          else return value;
        }
      },
      {
        "mData": "id",
        "sWidth": "26px",
        "bSearchable": false,
        "bSortable": false,
        "mRender": function(value, action, row) {
          if( 'display' !== action ) return value;

          if (row["unassignable"]) {
            return '<input type="checkbox"/>';
          } else {
            var icon = 'ui-icon-cancel';
            return '<em class="ui-icon ' + icon + '" title="Group Assignment">&nbsp;</em>';
          }
        }
      }
    ]
  },

  setupTable: function(url) {
    SNAPVOLUMES.assignment_list.dtable = $(SNAPVOLUMES.assignment_list.id('table')).dataTable({
      "sAjaxSource": url,
      "sAjaxDataProp": "assignments",
      "bProcessing": false,
      "bServerSide": false,
      "bAutoWidth": false,
      "bSortClasses": false,
      "bJQueryUI": true,
      "bFilter": false,
      "bPaginate": false,
      "bLengthChange": false,
      "iDisplayLength": 100,
      "aaSorting": [],
      "oLanguage": SNAPVOLUMES.data_tables.defaultLanguageConfig({
        "sEmptyTable": "No assignments",
        "sLoadingRecords": 'Loading...',
        "sInfo": "_TOTAL_ assignments" + SNAPVOLUMES.data_tables.selectedInfo(SNAPVOLUMES.assignment_list.prefix + '_select_info')
      }),
      "aoColumns": SNAPVOLUMES.assignment_list.columns(),

      "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
        var checkbox = $("input[type=checkbox]", nRow);
        SNAPVOLUMES.assignment_list.observe_checkbox(checkbox);
      },

      "fnInitComplete": function() {
        SNAPVOLUMES.assignment_list.observe_checkbox(SNAPVOLUMES.assignment_list.id('check_all'));
        SNAPVOLUMES.data_tables.jumpTop(this).setupFilter(this);
        SNAPVOLUMES.assignment_list.observeOrderCheckbox(this, url);
        SNAPVOLUMES.assignment_list.setupOrdering(this);
      },

      fnFooterCallback: function(nFoot, aData, iStart, iEnd, aiDisplay) {
        setTimeout(function() {
          SNAPVOLUMES.assignment_list.toggle_check_actions();
        }, 5);
      }
    });
  },

  setupOrdering: function(table, url) {
    $('#order_chkbox').prop('checked', false);

    if (table.fnGetNodes().length > 0) {
      $('#order_span').show();
    }

    var tbody = $(table).find('TBODY');
    tbody.disableSelection();

    tbody.find('TR').each(function() {
        var row = table.fnGetData(this);
        if (row && row.sortable) $(this).addClass('sortable');
      }
    );

    if (table.fnGetNodes().length && SNAPVOLUMES.assignment_list.isOverrideOrdering(table)) {
      SNAPVOLUMES.assignment_list.enableSorting(table);
    }

    return this;
  },

  isOverrideOrdering: function(dtable) {
    var override = false;
    dtable.find('TBODY > TR').each(
      function(index) { if (dtable.fnGetData(this).sortable && dtable.fnGetData(this).priority != 0) override = true; }
    );

    return override;
  },

  orderChanged: function(event, ui) {
    SNAPVOLUMES.assignment_list.setOrdering(SNAPVOLUMES.assignment_list.dtable);
  },

  setOrdering: function(dtable) {
    var order_data = [];
    dtable.find('TBODY > TR.sortable').each(
      function(index) { order_data.push(dtable.fnGetData(this).id); }
    );
    dtable.find('TBODY > TR.sortable SPAN.affordance').switchClass('ui-icon-arrowthick-2-n-s', 'ui-icon-transfer-e-w');

    return $.ajax({
      type: "post",
      data: order_data.join(),
      url: "/cv_api/assignments/update_priorities",
      cache: false,
      error: function(json) {
        SNAPVOLUMES.flash.error('Unable to save assignment order.');
      },
      complete: function(response) {
        SNAPVOLUMES.assignment_list.dtable.find('TBODY > TR.sortable SPAN.affordance').switchClass('ui-icon-transfer-e-w', 'ui-icon-arrowthick-2-n-s');
      }
    });
  },

  clearOrdering: function(dtable) {
    var ids = [];
    dtable.find('TBODY > TR.sortable').each(
      function(index) { ids.push(dtable.fnGetData(this).id); }
    );

    return $.ajax({
      type: "post",
      data: ids.join(),
      url: "/cv_api/assignments/clear_priorities",
      cache: false,
      error: function(json) {
        SNAPVOLUMES.flash.error('Unable to save assignment order.');
      }
    });
  },

  startSorting: function(event, ui) {
    var dt = SNAPVOLUMES.assignment_list.dtable;
    SNAPVOLUMES.assignment_list.enableSorting(dt);
    SNAPVOLUMES.assignment_list.setOrdering(dt);
  },

  enableSorting: function(table) {
    $('#order_chkbox').prop('checked', true);

    var tbody = $(table).find('TBODY');
    tbody.disableSelection();

    tbody.find('TR').each(
      function() {
        var row = table.fnGetData(this);
        if (row && row.sortable) $(this).addClass('sortable');
      }
    );

    tbody.sortable({
      items: '> TR.sortable',
      update: SNAPVOLUMES.assignment_list.orderChanged,
      disabled: false
    });

    SNAPVOLUMES.assignment_list.addSortingAffordance(table);

    return this;
  },

  addSortingAffordance: function(table) {
    var tbody = $(table).find('TBODY');

    tbody.find('TR').each(function(){
      var spanContent = ' ';
      if ($(this).hasClass("sortable")) {
        spanContent = '<span class="affordance ui-icon ui-icon-arrowthick-2-n-s" style="display: inline-block; opacity: .5">';
        $(this).css({borderSpacing: "0 2px", backgroundColor: "#E0EEFF"});
      }

      $(this).find('td').eq(0).before('<td style="padding-right: 0">' + spanContent + '</td>');
    });

    $(table).find('TH').first().attr('colspan',2);

    return this;
  },

  disableSorting: function(table, url) {
    $('#order_chkbox').attr("disabled", true).prop('checked', false);

    var tbody = $(table).find('TBODY');
    tbody.sortable( "disable" );
    tbody.enableSelection();

    $(table).find('TH').first().attr('colspan',1);

    var dt = SNAPVOLUMES.assignment_list.dtable;
    SNAPVOLUMES.assignment_list.clearOrdering(dt);

    dt.fnClearTable();
    $(table).find(".dataTables_empty").text('Reloading...');
    dt.fnReloadAjax(undefined, function() {
      $('#order_chkbox').removeAttr("disabled");
    });

    return this;
  },

  observeOrderCheckbox: function(table, url) {
    $("#order_chkbox").off().on("change", function() {
      if ($("#order_chkbox").is(":checked")) {
        SNAPVOLUMES.assignment_list.enableSorting(table).setOrdering(table);
      } else {
        SNAPVOLUMES.assignment_list.disableSorting(table, url);
      }
    });
    return this;
  },

  observe_checkbox: function(checkbox) {
    $(checkbox).off("change").on("change", function() {
      SNAPVOLUMES.data_tables.toggleCheckbox(SNAPVOLUMES.assignment_list.dtable, this, 'check-all');
      SNAPVOLUMES.assignment_list.toggle_check_actions();
    });
  },

  toggle_check_actions: function() {
    var check_count = SNAPVOLUMES.data_tables.checkedCount(SNAPVOLUMES.assignment_list.dtable);
    SNAPVOLUMES.data_tables.showChecked(SNAPVOLUMES.assignment_list.id('select_info'), check_count);

    if( check_count ) {
      $(SNAPVOLUMES.assignment_list.id('submit')).button("enable").show();
    } else {
      $(SNAPVOLUMES.assignment_list.id('submit')).button("disable").hide();
    }
  },

  checked_rows: function() {
    return SNAPVOLUMES.data_tables.checkedRows(SNAPVOLUMES.assignment_list.dtable);
  }
};

SNAPVOLUMES.datastores = {
  labels: {
    "Shared": "Shared Storage (recommended)",
    "Local": "Local Storage"
  },

  populate_appstack_form: function(dropdown_selector, path_input_selector, template_selector) {
    return SNAPVOLUMES.datastores.get_all().done(
      function(json) {
        SNAPVOLUMES.datastores
          .clearDropdown(dropdown_selector)
          .populateDropdowns(dropdown_selector, json.datastores)
          .selectCurrentFromCurrent(dropdown_selector, json.appstack_storage, json.datacenter)
          .populatePath(path_input_selector, json.appstack_path)
          .setTemplatePath(template_selector, json.appstack_template_path);
      }
    );
  },

  populate_writable_form: function(dropdown_selector, path_input_selector, template_selector) {
    var query = template_selector ? 'groups' : '';

    return SNAPVOLUMES.datastores.get_all(query).done(
      function(json) {
        SNAPVOLUMES.datastores
          .clearDropdown(dropdown_selector)
          .populateDropdowns(dropdown_selector, json.datastores)
          .selectCurrentFromCurrent(dropdown_selector, json.writable_storage, json.datacenter)
          .populatePath(path_input_selector, json.writable_path)
          .setTemplatePath(template_selector, json.writable_template_path);
      }
    );
  },

  populateDropdowns: function(selector, datastore_list) {
    var dropdown = $(selector);

    var grp = {"Grouped Storage": null, "Shared": null, "Managed File Shares": null, "Local": null};

    $.map(datastore_list, function(ds) {
      if (!grp[ds.category]) grp[ds.category] = SNAPVOLUMES.datastores.buildOptGroup(ds.category);

      grp[ds.category].append(SNAPVOLUMES.datastores.buildOption(ds));
    });

    SNAPVOLUMES.datastores.buildGroupDropdown(dropdown, grp);

    return this;
  },

  populatePath: function(selector, path) {
    $(selector).prop("disabled", false).val(path || '');
    return this;
  },

  setTemplatePath: function(selector, path) {
    $(selector).data("path", path);
    return this;
  },

  selectCurrentFromCurrent: function(selector, datastore, datacenter) {
    SNAPVOLUMES.datastores.selectCurrent(selector, datastore, datacenter);
    return this;
  },

  selectCurrent: function(selector, select_value, datacenter) {
    if (select_value && select_value.length > 0) {
      $(selector).find("option").each(function() {
        var element = $(this);

        if( element.val() == select_value ) {
          element.prop('selected', true);
          return this; // break
        }

        if (element.data("datastore") == select_value && element.data("datacenter") == datacenter ) {
          element.prop('selected', true);
          return this; // break
        }
      });
    }

    return this;
  },

  buildGroupDropdown: function(dropdown_selector, optgroups) {
    $.each(optgroups, function(dtype, opts) {
      $(dropdown_selector).append(opts);
    });

    $(dropdown_selector).prop("disabled", false);

    return this;
  },

  buildOption: function(ds) {
    var note = ds.note ? $("<small></small>").css({color: "gray"}).text(" (" + ds.note + ")") : "";
    var status = ds.accessible ? "" : $("<small></small>").css({color: "gray"}).text(" [Inaccessible]");

    return $("<option></option>")
      .val(ds.identifier)
      .prop("disabled", !ds.accessible)
      .data("description", ds.description)
      .data("datacenter", ds.datacenter)
      .data("datastore", ds.name)
      .data("template_storage", ds.template_storage)
      .append(ds.display_name, note, status);
  },

  buildOptGroup: function(label) {
    return $("<optgroup></optgroup>").attr("label", SNAPVOLUMES.datastores.labels[label] || label);
  },

  clearDropdown: function(selector, text) {
    $(selector).empty().append($("<option></option>").val("").text(text || 'Choose a storage location:'));
    return this;
  },

  get_all: function(get_params) {
    return $.ajax({
      url: "/cv_api/datastores?" + (get_params || ""),
      cache: false,
      dataType: 'json'
    });
  }
};

SNAPVOLUMES.templates = {

  populate: function(selector_id, datastore, path, templates_path) {
    return SNAPVOLUMES.templates.getAll(datastore, path, templates_path).done(
      function(json) {
        SNAPVOLUMES.templates
          .buildDropdown(selector_id, json.templates)
          .autoSelectFirstIfOne(selector_id);
      }
    );
  },

  autoSelectFirstIfOne: function(selector_id) {
    var dropdown = $(selector_id);

    if (dropdown.prop('length') == 2) dropdown.children("option").first().next().prop('selected', true);
    return this;
  },

  buildDropdown: function(selector_id, templates) {
    var dropdown = $(selector_id).empty();

    if (!$.isEmptyObject(templates)) {
      dropdown.append(
        $("<option></option>").val("").text('Choose a template:')
      );

      $.map(templates, function(template) {
        dropdown.append(
          $("<option></option>")
            .val(template.path)
            .text(template.path + template.sep + template.name)
            .data("template_path", template.path)
            .data("template_name", template.name)
            .prop("disabled", template.uploading)
        );
      });
    } else {
      dropdown.append(
        $("<option></option>").val("").text('No templates found.')
      );
    }

    dropdown.prop('disabled', false);
    return this;
  },

  clearDropdown: function(selector_id, text) {
    $(selector_id).empty().append($("<option></option>").val("").text(text || ''));
    return this;
  },

  getAll: function(datastore, path, templates_path) {
    return $.ajax({
      url: "/cv_api/templates",
      cache: false,
      dataType: 'json',
      data: {"datastore": datastore, "path": path, "templates_path": templates_path}
    });
  }
};

SNAPVOLUMES.resource_pools = {
  populate_dropdown: function(selector, default_text) {
    SNAPVOLUMES.resource_pools.get_all().done(function(json) {
      SNAPVOLUMES.resource_pools.rebuild_dropdown(selector, json, default_text);
    });
  },

  get_all: function() {
    return $.ajax({
      url: "/cv_api/resource_pools",
      cache: false,
      dataType: 'json'
    });
  },

  rebuild_dropdown: function(selector, json, default_text) {
    var data = json.resource_pools;

    var dropdown = $(selector).empty();

    dropdown.append(
      $("<option></option>").val("").text(default_text || "")
    );

    if (!$.isEmptyObject(data)) {
      $.each(data, function(k, v) {
        dropdown.append(
          $("<option></option>")
            .val(v.ref)
            .text(v.name)
        );
      });
      dropdown.prop('disabled', false);
    }
  }
};

SNAPVOLUMES.customization_profiles = {
  populate_dropdown: function(selector, default_text) {
    SNAPVOLUMES.customization_profiles.get_all().done(function(json) {
      SNAPVOLUMES.customization_profiles.rebuild_dropdown(selector, json, default_text);
    });
  },

  get_all: function() {
    return $.ajax({
      url: "/cv_api/customization_profiles",
      cache: false,
      dataType: 'json'
    });
  },

  rebuild_dropdown: function(selector, json, default_text) {
    var data = json.customization_profiles;

    var dropdown = $(selector).empty();

    dropdown.append(
      $("<option></option>").val("").text(default_text || "")
    );

    if (!$.isEmptyObject(data)) {
      $.each(data, function(k, v) {
        dropdown.append(
          $("<option></option>")
            .val(v.name)
            .text("[" + v.os_type + "] " + v.name)
        );
      });
      dropdown.prop('disabled', false);
    }
  }
};

SNAPVOLUMES.edit_assignments = {
  submitRequest: function(params) {
    return $.ajax({
      type: "post",
      data: params,
      url: "/cv_api/assignments",
      cache: false,
      success: function(json) {
        SNAPVOLUMES.flash.jsonResponse(json);
        SNAPVOLUMES.jobs.checkPending();
      },
      complete: function(response) {
        SNAPVOLUMES.progress_bar.hide();
      }
    });
  }
};

$(function() {
  $('#main_nav').find('li').on({
    "mouseenter": function() { $(this).addClass("ui-state-hover"); },
    "mouseleave": function() { $(this).removeClass("ui-state-hover"); }
  });

  $("#dialog_appstack, #dialog_detail").dialog({
    autoOpen: false,
    dialogClass: 'ui-shadow',
    show: "clip",
    resizable: false,
    width: 550,
    modal: true,
    open: function() {
      $(".ui-dialog-titlebar-close").show();
      $(this).next(".ui-dialog-buttonpane").find("button.ui-button").focus();
    }
  });

  $("#dialog_help").dialog({
    autoOpen: false,
    dialogClass: 'ui-shadow',
    resizable: false,
    width: 500,
    open: function() {
      $(".ui-dialog-titlebar-close").show();
      $(this).next(".ui-dialog-buttonpane").find("button.ui-button").focus();
    }
  });

  $.getJSON("/cv_api/version", function(data) { $("#version").html(data.version); });
});

(function($) {
  if ($.fn.dataTableExt) {
    $.fn.dataTableExt.oPagination.iFullNumbersShowPages = 1;
  }

  $.eventReport = function(selector, root) {
    var s = [];
    var count = 0;
    $(selector || '*', root).andSelf().each(function() {
      count += 1;
      // the following line is the only change
      var e = $.data(this, 'events');
      if (!e) return;
      s.push(this.tagName);
      if (this.id) s.push('#', this.id);
      if (this.className) s.push('.', this.className.replace(/ +/g, '.'));
      for (var p in e) {
        var r = e[p],
          h = r.length - r.delegateCount;
        if (h)
          s.push('\n', h, ' ', p, ' handler', h > 1 ? 's' : '');
        if (r.delegateCount) {
          for (var q = 0; q < r.length; q++)
            if (r[q].selector) s.push('\n', p, ' for ', r[q].selector);
        }
      }
      s.push('\n\n');
    });
    //svlog("eventReport count " + count + " for selector: " + selector);
    return s.join('');
  };

  $.fn.eventReport = function(selector) {
    svlog("eventReport", selector, this);
    return $.eventReport(selector, this);
  };
})(jQuery);

