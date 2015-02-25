$("#scatter-load").empty();

var yesData = [
    {"hour": 1, "count": 5},
    {"hour": 2, "count": 9},
    {"hour": 3, "count": 8},
    {"hour": 4, "count": 8},
    {"hour": 5, "count": 9},
    {"hour": 6, "count": 7},
    {"hour": 7, "count": 13},
    {"hour": 8, "count": 11},
    {"hour": 9, "count": 9},
    {"hour": 10, "count": 4},
    {"hour": 11, "count": 8},
    {"hour": 12, "count": 7},
    {"hour": 13, "count": 7},
    {"hour": 14, "count": 8},
    {"hour": 15, "count": 7},
    {"hour": 16, "count": 6},
    {"hour": 17, "count": 15},
    {"hour": 18, "count": 13},
    {"hour": 19, "count": 7},
    {"hour": 20, "count": 14},
    {"hour": 21, "count": 4},
    {"hour": 22, "count": 1},
    {"hour": 23, "count": 9},
    {"hour": 24, "count": 10}
];

var noData = [
    {"hour": 1, "count": 15},
    {"hour": 2, "count": 19},
    {"hour": 3, "count": 18},
    {"hour": 4, "count": 18},
    {"hour": 5, "count": 19},
    {"hour": 6, "count": 17},
    {"hour": 7, "count": 33},
    {"hour": 8, "count": 31},
    {"hour": 9, "count": 19},
    {"hour": 10, "count": 14},
    {"hour": 11, "count": 18},
    {"hour": 12, "count": 17},
    {"hour": 13, "count": 17},
    {"hour": 14, "count": 18},
    {"hour": 15, "count": 17},
    {"hour": 16, "count": 16},
    {"hour": 17, "count": 25},
    {"hour": 18, "count": 33},
    {"hour": 19, "count": 17},
    {"hour": 20, "count": 24},
    {"hour": 21, "count": 14},
    {"hour": 22, "count": 11},
    {"hour": 23, "count": 19},
    {"hour": 24, "count": 20}
];

function renderCharts(data1, data2) {
    var margins = {
        "left": 40,
        "right": 30,
        "top": 30,
        "bottom": 30
    };

    var width = 500;
    var height = 500;

    var colors = d3.scale.category10();

    var svg = d3.select("#scatter-load").append("svg").attr("width", width).attr("height", height).append("g")
        .attr("transform", "translate(" + margins.left + "," + margins.top + ")");

    var x = d3.scale.linear()
        .domain(d3.extent(d3.merge(data1, data2), function (d) {
            return d.hour;
        }))
        .range([0, width - margins.left - margins.right]);

    var y = d3.scale.linear()
        .domain(d3.extent(d3.merge(data1, data2), function (d) {
            return d.count;
        }))
        .range([height - margins.top - margins.bottom, 0]);

    svg.append("g").attr("class", "x axis").attr("transform", "translate(0," + y.range()[0] + ")");
    svg.append("g").attr("class", "y axis");

    svg.append("text")
        .attr("fill", "#414241")
        .attr("text-anchor", "end")
        .attr("x", width / 2)
        .attr("y", height - 35)
        .text("Hour");


    var xAxis = d3.svg.axis().scale(x).orient("bottom").tickPadding(2);
    var yAxis = d3.svg.axis().scale(y).orient("left").tickPadding(2);

    svg.selectAll("g.y.axis").call(yAxis);
    svg.selectAll("g.x.axis").call(xAxis);

    var hourlyData = svg.selectAll("g.node").data(data, function (d) {
        return d.hour;
    });

    var hourlyDataGroup = hourlyData.enter().append("g").attr("class", "node")
        .attr('transform', function (d) {
            return "translate(" + x(d.hour) + "," + y(d.count) + ")";
        });

    hourlyDataGroup.append("circle")
        .attr("r", 5)
        .attr("class", "dot")
        .style("fill", function (d) {
            return colors(d.manufacturer);
        });
}

$(document).ready(function () {
    renderCharts(yesData, noData);
});
