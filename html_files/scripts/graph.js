const nodes = graphData.map(node => ({
    id: node.id,
    name: node.name,
    type: node.type
}));

const links = [];
graphData.forEach(node => {
    node.edges.forEach(edge => {
        links.push({ source: node.id, target: edge });
    });
});

// var iframe = document.getElementById("graphFrame");
var iframe = window.frameElement;
const width = iframe.offsetWidth-20;
const height = iframe.offsetHeight - 20;

const svg = d3.select("body").append("svg")
    .attr("width", width)
    .attr("height", height);

const simulation = d3.forceSimulation(nodes)
    .force("link", d3.forceLink(links).id(d => d.id).distance(200))
    .force("charge", d3.forceManyBody().strength(-500))
    .force("center", d3.forceCenter(width / 2, height / 2));

const link = svg.append("g")
    .attr("class", "links")
    .selectAll("line")
    .data(links)
    .enter().append("line")
    .attr("class", "link");

const node = svg.append("g")
    .attr("class", "nodes")
    .selectAll("g")
    .data(nodes)
    .enter().append("g")
    .attr("class", "node");

node.append("circle")
    .attr("r", 10);

node.append("text")
    .attr("dy", -15)
    .text(d => `${d.type}: ${d.name}`);

simulation.on("tick", () => {
    link
        .attr("x1", d => d.source.x)
        .attr("y1", d => d.source.y)
        .attr("x2", d => d.target.x)
        .attr("y2", d => d.target.y);

    node
        .attr("transform", d => `translate(${d.x},${d.y})`);
});

svg.append("defs").selectAll("marker")
    .data(["end"])
    .enter().append("marker")
    .attr("id", "end")
    .attr("viewBox", "0 -5 10 10")
    .attr("refX", 15)
    .attr("refY", 0)
    .attr("markerWidth", 6)
    .attr("markerHeight", 6)
    .attr("orient", "auto")
    .append("path")
    .attr("d", "M0,-5L10,0L0,5")
    .attr("fill", "#666");

link.attr("marker-end", "url(#end)");