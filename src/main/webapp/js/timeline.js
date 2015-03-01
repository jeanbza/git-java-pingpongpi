$("#scatter-load").empty();

function renderCharts(data) {
    var margin = {top: 20, right: 20, bottom: 30, left: 40},
        width = 960 - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom;

    var x = d3.scale.linear()
        .range([0, width]);

    var y = d3.scale.linear()
        .range([height, 0]);

    var z = d3.scale.category10();

    var svg = d3.select("#scatter-load").append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var seriesNames = d3.keys(data[0])
        .filter(function (d) {
            return d !== "hour";
        });

    var series = seriesNames.map(function (series) {
        return data.map(function (d) {
            return {x: +d.hour, y: +d[series]};
        });
    });

    x.domain(d3.extent(d3.merge(series), function (d) {
        return d.x;
    })).nice();
    y.domain(d3.extent(d3.merge(series), function (d) {
        return d.y;
    })).nice();

    svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.svg.axis().scale(x).orient("bottom"));

    svg.append("g")
        .attr("class", "y axis")
        .call(d3.svg.axis().scale(y).orient("left"));

    svg.selectAll(".series")
        .data(series)
        .enter().append("g")
        .attr("class", "series")
        .style("fill", function (d, i) {
            return i % 2 == 0 ? "green" : "red";
        })
        .selectAll(".point")
        .data(function (d) {
            return d;
        })
        .enter().append("circle")
        .attr("class", "point")
        .attr("r", 4.5)
        .attr("cx", function (d) {
            return x(d.x);
        })
        .attr("cy", function (d) {
            return y(d.y);
        });
}

function normalizeDailyActivities(dailyActivities) {
    var normalized = [];

    for (var hour = 0; hour < 24; hour++) {
        normalized.push({"hour": hour});
    }

    for (var dailyActivity of dailyActivities) {
        for (var hour = 0; hour < 24; hour++) {
            normalized[hour][dailyActivity.date + "_active"] = [];
            normalized[hour][dailyActivity.date + "_inactive"] = [];
        }

        for (var hour = 0; hour < 24; hour++) {
            normalized[hour][dailyActivity.date+"_active"].push(dailyActivity.hourlyActive[hour]);
            normalized[hour][dailyActivity.date+"_inactive"].push(dailyActivity.hourlyInactive[hour]);
        }
    }

    return normalized;
}

$(document).ready(function () {
    $.ajax({
        url: "/pingpong/dailyActivity",
        success: function(data) {
            var jsonData = JSON.parse(data);
            var normalizedJsonData = normalizeDailyActivities(jsonData);
            renderCharts(normalizedJsonData);
        }
    });
});
