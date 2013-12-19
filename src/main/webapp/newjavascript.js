/* 
 * Copyright (C) 2013 alberto
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */



$("#docs").tabs();
$("#filters").tabs({
    show: function(event, ui) {
        var tab = ui.tab.toString().split('#')[1];
        if (tab == 'dadiv') {
            positionCurtains();
            setBarsPositions();

        }
    }
});
$(document).ready(function() {
    $.get("da.vm", function(data) {
        $("#dadiv").html(data);
        if ($("#dadiv").length == 0) {
            $("#dali").remove();
        } else {
            resizeAll();
            initDateAxis();
            $("#content-resizable").css("height", (containerHeight + 7) + "px");
            daScrollToMax();
        }
    });

    var w;
    var w1 = $(window).height() -
            $("#header").height() -
            $("#footer").outerHeight(true) - 2;
    $("#split").css("height", w1);
    w = w1 - $("#docs>ul").outerHeight(true) - 35;
    $("#docs_content>div.content").css("height", w);
    alert(w);
    w = w1 - $("#filters>ul").outerHeight(true) - 16;
    $("#facets").css("height", w);

    if ($("#content-resizable").length > 0) {

        //w = w - $("#da-inputs").outerHeight(true);
        w = w - 42;
        $("#content-resizable").css("height", w);
        resizeDateAxisContent();
        setMaxResize($("#content-resizable").height());
    }

    sp = $("#split").layout({
        west: {
            size: 300,
            spacing_closed: 5,
            spacing_open: 5,
            togglerLength_closed: '100%',
            togglerLength_open: '100%',
            togglerAlign_open: "top",
            togglerAlign_closed: "top",
            togglerTip_closed: '<fmt:message bundle="${lctx}">item.showhide</fmt:message>',
            togglerTip_open: '<fmt:message bundle="${lctx}">item.showhide</fmt:message>',
            onopen_end: function() {
                setColumnsWidth();
            },
            onclose_end: function() {
                setColumnsWidth();
            }
        },
        center: {
            spacing_closed: 5,
            spacing_open: 5,
            onresize: function() {
                setColumnsWidth();
            }
        }

    });
    translateCollections();
    getExtInfo();
    getCollapsedPolicy();
    $('.loading_docs').hide();

    $('#docs_content>div.content').bind('scroll', function(event) {
        if ($('#docs_content .more_docs').length > 0) {
            var id = $('#docs_content .more_docs').attr('id');
            if (isScrolledIntoView($('#' + id), $('#docs_content>div.content'))) {
                getMoreDocs(id);
            }
        }
    });

    if ($('#docs .more_docs').length > 0) {
        var id = $('#docs .more_docs').attr('id');
        if (isScrolledIntoWindow($('#' + id))) {
            //getMoreDocs(id);
        }
    }


    setColumnsWidth();
    checkHeight(0);
    $(".resultText>a").css("color", $("#docs>ul>li.ui-state-active a").first().css("color"));
    $(window).resize(function(event, viewerOptions) {
        resizeAll();
    });
});

function translateCollections() {
    $("div.collections").mouseenter(function() {
        $(this).children("div.cols").show();
    });
    $("div.collections").mouseleave(function() {
        $(this).children("div.cols").hide();
    });
    $("div.collection").each(function() {
        var id = $(this).text();
        var title = "";
        title = collectionsDict[id];
        $(this).html(title);
    });
    // alert(a);
}

function resizeAll() {
    var w;
    var w1 = $(window).height() -
            $("#header").height() -
            $("#footer").outerHeight(true) - 2;
    $("#split").css("height", w1);
    w = w1 - $("#docs>ul").outerHeight(true) - 35;
    $("#docs_content>div.content").css("height", w);
    w = w1 - $("#filters>ul").outerHeight(true) - 16;
    $("#facets").css("height", w);

    if ($("#content-resizable").length > 0) {
        w = w - 42;
        $("#content-resizable").css("height", w);
        resizeDateAxisContent();
        setMaxResize($("#content-resizable").height());
    }
    checkHeight(0);
}

function changeResSelection(o) {
    var id = $(getResultElement(o)).attr("id");
    var escapedId = id.substring(4).replace(/\//g, '-');
    if ($(o).is(":checked")) {
        var label = $(jq(id) + " div.resultText>a>b").html();
        addToContextMenuSelection(escapedId, label);
    } else {
        removeFromContextMenuSelection(escapedId);
    }
}

function refreshResSelection() {
    $('.search_result>input').each(function() {
        changeResSelection(this);
    });
}

function selectAll() {
    $('.search_result>input').attr('checked', true);
    refreshResSelection();
}

function selectNone() {
    $('.search_result>input').attr('checked', false);
    refreshResSelection();
}

function selectInvert() {
    $('.search_result>input').each(function() {
        $(this).attr('checked', !$(this).is(':checked'));
    });

    refreshResSelection();
}




function toggleColumns(post) {
    $('.cols').toggle();
    setColumnsWidth();
    var sloupce = $('#cols2').is(':visible') ? 1 : 2;


    if (post) {
        var key = "columns";
        var val = sloupce;

        $.get("profile.vm?action=PREPARE_FIELD_TO_SESSION&key=columns&field=" + sloupce, function() {
        });

        /*                           
         (new Profile()).modify(function(data) {
         var results = data["results"];
         if (!results) {
         results = {'columns':sloupce};
         } else {
         results['columns'] = sloupce;
         }
         data['results'] = results;
         
         return data;
         });
         */
    }

}

function setColumnsWidth() {
    var margin =
            parseInt($('.search_result:first').css("padding-left").replace("px", "")) +
            parseInt($('.search_result:first').css("padding-right").replace("px", "")) +
            parseInt($('#docs_content').css("padding-left").replace("px", "")) +
            parseInt($('#docs_content').css("padding-right").replace("px", ""));
    var w = $('#offset_0').width();
    if ($('#cols2').is(':visible')) {
        w = w - margin;
    } else {
        w = w / 2 - margin;
    }
    $('.search_result').css('width', w);

    $('.search_result .resultThumb').css('max-width', w / 2);
}

function checkHeight(offset) {
    var divs = $('#offset_' + offset + '>div.search_result').length;
    var left;
    var right;
    var max;
    for (var i = 1; i < divs; i = i + 2) {
        left = $('#offset_' + offset + '>div.search_result')[i - 1];
        right = $('#offset_' + offset + '>div.search_result')[i];
        checkRowHeight(left, right);
    }
}

function checkRowHeight(left, right) {
    var max;
    var id1 = $(left).attr('id');
    if ($(right).length > 0) {
        var id2 = $(right).attr('id');
        max = Math.max($(jq(id1) + '>div.result').height(), $(jq(id2) + '>div.result').height());
        //max = Math.max(max, $(jq(id2)+' img.th').height());
        max = Math.max(max, 140);
    } else {
        max = Math.max($(jq(id1) + '>div.result').height(), 140);
    }
    max = max + $(jq(id1) + '>div.collapse_label').height();
    $(left).css('height', max);
    $(right).css('height', max);
}

function getResultElement(el) {
    var div = $(el);
    while (!$(div).hasClass('search_result') && $(div).attr("id") != "docs_content" && $(div).parent().length > 0) {
        div = $(div).parent();
    }
    return div;
}

function resultThumbLoaded(obj) {
    checkRowHeightByElement(obj);
}

function checkRowHeightByElement(el) {
    var div = getResultElement(el);
    if ($(div).hasClass('0')) {
        var div2 = $(div).prev();
        checkRowHeight(div2, div);
    } else {
        var div2 = $(div).next();
        checkRowHeight(div, div2);
    }
}

var policyConf = true;
function getCollapsedPolicy() {
    $(".search_result>input.root_pid").each(function() {
        var root_pid = $(this).val();
        var res_id = $(this).parent().attr("id");
        var url = "results/collapsed_policy.vm?root=" + root_pid;
        $.get(url, function(data) {
            var src = "img/empty.gif";
            var title = "dostupnost.";
            if (policyConf) {
                if (data == '0') {
                    //mix
                    src = 'img/mixed.png';
                    title += "mixed";
                } else if (data == '1') {
                    //public
                    src = 'img/public.png';
                    title += "public";
                }
            } else if (data == '2') {
                //private
                src = 'img/lock.png';
                title += "private";
            }
            $(jq(res_id) + ' img.dost').attr('src', src);
            $(jq(res_id) + ' img.dost').attr('title', dictionary[title]);
        });
    });
}

function getExtInfo() {
    $(".extInfo:hidden").each(function() {
        var info = $(this);
        //$(info).removeClass("extInfo");
        var pid_path = $(info).text();
        if (pid_path.indexOf("/") > 0) {
            var url = "results/extendedInfo.vm?pid_path=" + pid_path;
            $.get(url, function(data) {
                $(info).html(data);
                $(info).show();
                checkRowHeightByElement(info);
            });
        }
    });
}

function getMoreDocs(id) {
    var offset = id.split('_')[1];
    var page = new PageQuery(window.location.search);
    page.setValue("offset", offset);
    var url = "search.vm?onlymore=true&" + page.toString();
    $.get(url, function(data) {
        $(jq(id)).html(data);
        $(jq(id)).removeClass('more_docs');
        getExtInfo();
        $('.loading_docs').hide();
        translateCollections();
        checkHeight(offset);
        setColumnsWidth();
    });
}

function getPidPath(id) {
    return id.split('_')[1];
}

function sortByTitle(dir) {
    $('#sort').val('title_sort ' + dir);
    $('#forProfile').val('sortbytitle');
    $('#forProfile_sorting_dir').val(dir);
    $('#searchForm').submit();
}

function sortByRank() {
    $('#sort').val('level asc, score desc');
    $('#forProfile').val('sortbyrank');
    $('#searchForm').submit();
}

function addFilter(field, value) {
    var page = new PageQuery(window.location.search);
    page.setValue("offset", "0");
    page.setValue("forProfile", "facet");

    var f = "fq=" + field + ":\"" + value + "\"";
    if (window.location.search.indexOf(f) == -1) {
        window.location = "search.vm?" +
                page.toString() + "&" + f;
    }
}

function toggleCollapsed(root_pid, pid, offset) {
    $(jq("res_" + root_pid) + " div.uncollapsed").toggle();
    $(jq('uimg_' + root_pid)).toggleClass('uncollapseIcon');
    if ($(jq("res_" + root_pid) + " div.uncollapsed").html() == "") {
        uncollapse(root_pid, pid, offset);
    }
}

function uncollapse(root_pid, pid, offset) {
    var page = new PageQuery(window.location.search);
    page.setValue("offset", offset);
    var url = "results/uncollapse.vm?rows=10&" + page.toString() +
            "&type=uncollapse&collapsed=false&root_pid=" + root_pid +
            "&pid=" + pid +
            "&fq=root_pid:\"" + root_pid.split("_")[1] + "\"" + "&fq=-PID:\"" + pid + "\"";
    $.get(url, function(xml) {
        $(jq("res_" + root_pid) + " div.uncollapsed").html(xml);
        $(jq("res_" + root_pid) + " div.uncollapsed").scrollTop(0);
    });
}