$("#scatter-load").empty();

var hourlyData = [
    {"hour": 1,  "lowerCount": 15, "upperCount": 5},
    {"hour": 2,  "lowerCount": 19, "upperCount": 9},
    {"hour": 3,  "lowerCount": 18, "upperCount": 8},
    {"hour": 4,  "lowerCount": 18, "upperCount": 8},
    {"hour": 5,  "lowerCount": 19, "upperCount": 9},
    {"hour": 6,  "lowerCount": 17, "upperCount": 7},
    {"hour": 7,  "lowerCount": 33, "upperCount": 13},
    {"hour": 8,  "lowerCount": 31, "upperCount": 11},
    {"hour": 9,  "lowerCount": 19, "upperCount": 9},
    {"hour": 10,  "lowerCount": 14,  "upperCount": 4},
    {"hour": 11,  "lowerCount": 18,  "upperCount": 8},
    {"hour": 12,  "lowerCount": 17,  "upperCount": 7},
    {"hour": 13,  "lowerCount": 17,  "upperCount": 7},
    {"hour": 14,  "lowerCount": 18,  "upperCount": 8},
    {"hour": 15,  "lowerCount": 17,  "upperCount": 7},
    {"hour": 16,  "lowerCount": 16,  "upperCount": 6},
    {"hour": 17,  "lowerCount": 25,  "upperCount": 15},
    {"hour": 18,  "lowerCount": 33,  "upperCount": 13},
    {"hour": 19,  "lowerCount": 17,  "upperCount": 7},
    {"hour": 20,  "lowerCount": 24,  "upperCount": 14},
    {"hour": 21,  "lowerCount": 14,  "upperCount": 4},
    {"hour": 22,  "lowerCount": 11,  "upperCount": 1},
    {"hour": 23,  "lowerCount": 19,  "upperCount": 9},
    {"hour": 24,  "lowerCount": 2,  "upperCount": 10}
];

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
        })
        .sort();

    var series = seriesNames.map(function (series) {
        return data.map(function (d) {
            return {x: +d.hour, y: +d[series]};
        });
    });

    console.dir(seriesNames);
    console.dir(series);

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
            return z(i);
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

$(document).ready(function () {
    renderCharts(hourlyData);
});
