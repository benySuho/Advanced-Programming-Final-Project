const nodes = graphData.map(node => ({
    id: node.id,
    name: node.name,
    type: node.type
}));

const links = [];
graphData.forEach(node => {
    node.edges.forEach(edge => {
        links.push({source: node.id, target: edge});
    });
});

function getCircleCoordinates(n, width, height) {
    const coordinates = [];
    const angleStep = (2 * Math.PI) / n;
    const radius = Math.min(width, height) / 2 - 20;

    for (let i = 0; i < n; i++) {
        const angle = i * angleStep;
        const x = radius * Math.cos(angle) + width / 2;
        const y = radius * Math.sin(angle) + height / 2;
        coordinates.push({x: x, y: y});
    }

    return coordinates;
}

function getElementSizeByClass(className) {
    // Create a temporary element
    const tempElement = document.createElement('div');
    // Add the class to the temporary element
    tempElement.className = className;
    // Append the temporary element to the body (off-screen)
    tempElement.style.position = 'absolute';
    tempElement.style.visibility = 'hidden';
    document.body.appendChild(tempElement);
    // Get the computed style of the temporary element
    const computedStyle = window.getComputedStyle(tempElement);
    const width = parseInt(computedStyle.width, 10);
    const height = parseInt(computedStyle.height, 10);
    // Remove the temporary element from the DOM
    document.body.removeChild(tempElement);
    return {"width": width, "height": height};
}

function calculateLinkEnd(source, target, isX) {
    const dx = target.x - source.x;
    const dy = target.y - source.y;
    const angle = Math.atan2(dy, dx);

    if (source.type === 'Topic') {
        const offset = size.height / 2;
        return isX ? source.x + Math.cos(angle) * offset : source.y + Math.sin(angle) * offset;
    } else if (source.type === 'Agent') {
        const radius = 15;
        return isX ? source.x + Math.cos(angle) * radius : source.y + Math.sin(angle) * radius;
    }
}

// var iframe = document.getElementById("graphFrame");
var iframe = window.frameElement;
const width = iframe.offsetWidth - 20;
const height = iframe.offsetHeight - 20;
// console.log(width, height);
// console.log(nodes.length);
coordinates = getCircleCoordinates(nodes.length, width, height);
const svg = d3.select("#svgID")
    .attr("width", width)
    .attr("height", height);

const simulation = d3.forceSimulation(nodes)
    .force("link", d3.forceLink(links).id(d => d.id).distance(200))
    .force("charge", d3.forceManyBody().strength(-500))
    .force("center", d3.forceCenter(width / 2, height / 2))
    .force("collide", d3.forceCollide(30)); // Add collision force

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
    .attr("class", "node")
    .call(d3.drag()
        .on("start", dragstarted)
        .on("drag", dragged)
        .on("end", dragended));

const size = getElementSizeByClass('topic');
node.each(function (d) {
    if (d.type === 'Topic') {
        d3.select(this).append("rect")
            .attr("x", -size["width"] / 2)
            .attr("y", -size["height"] / 2)
            .attr("class", "topic");
    } else if (d.type === 'Agent') {
        d3.select(this).append("circle")
            .attr("class", "agent");
    }
});

node.append("text")
    .attr("dy", d => d.type === 'Topic' ? 5 : -25)
    .attr("text-anchor", "middle")
    .text(d => d.name);

simulation.on("tick", () => {
    link
        .attr("x1", d => d.source.x)  // Adjust the x-coordinate of the link's source)
        .attr("y1", d => d.source.y)
        .attr("x2", d => calculateLinkEnd(d.target, d.source, true))
        .attr("y2", d => calculateLinkEnd(d.target, d.source, false));

    node
        .attr("transform", d => `translate(${d.x},${d.y})`);

    node.each(d => {
        d.x = coordinates[d.index].x;
        d.y = coordinates[d.index].y;
    });
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

function dragstarted(event, d) {
    if (!event.active) simulation.alphaTarget(0.3).restart();
    d.fx = d.x;
    d.fy = d.y;
}

function dragged(event, d) {
    d.fx = event.x;
    d.fy = event.y;
}

function dragended(event, d) {
    if (!event.active) simulation.alphaTarget(0);
    d.fx = null;
    d.fy = null;
}