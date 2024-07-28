function drawGraph(graphData) {
    const canvas = document.getElementById('graphCanvas');
    const ctx = canvas.getContext('2d');
    const nodes = [];
    const edges = [];
    const nodeRadius = 20;

    graphData.forEach(node => {
        nodes.push({
            id: node.id,
            name: node.name,
            type: node.type,
            x: Math.random() * (canvas.width - 2 * nodeRadius) + nodeRadius,
            y: Math.random() * (canvas.height - 2 * nodeRadius) + nodeRadius,
            // edges: node.edges.map(edge => edge.name)
        });
        node.edges.forEach(edge => {
            edges.push({
                start: node.id,
                end: edge
            });
        })

    });


// Draw edges
    edges.forEach(edge => {
        const fromNode = nodes[edge.from];
        const toNode = nodes[edge.to];
        ctx.beginPath();
        ctx.moveTo(fromNode.x, fromNode.y);
        ctx.lineTo(toNode.x, toNode.y);
        ctx.stroke();
        drawArrowHead(fromNode.x, fromNode.y, toNode.x, toNode.y);
    });

// Draw nodes
    Object.keys(nodes).forEach(nodeName => {
        const node = nodes[nodeName];
        ctx.beginPath();
        ctx.arc(node.x, node.y, nodeRadius, 0, 2 * Math.PI);
        ctx.fillStyle = 'lightblue';
        ctx.fill();
        ctx.stroke();
        ctx.fillStyle = 'black';
        ctx.fillText(nodeName, node.x - nodeRadius / 2, node.y + nodeRadius / 2);
    });
}
// Draw arrowheads
    function drawArrowHead(fromX, fromY, toX, toY) {
        const headLength = 10;
        const dx = toX - fromX;
        const dy = toY - fromY;
        const angle = Math.atan2(dy, dx);
        ctx.beginPath();
        ctx.moveTo(toX, toY);
        ctx.lineTo(toX - headLength * Math.cos(angle - Math.PI / 6), toY - headLength * Math.sin(angle - Math.PI / 6));
        ctx.lineTo(toX - headLength * Math.cos(angle + Math.PI / 6), toY - headLength * Math.sin(angle + Math.PI / 6));
        ctx.lineTo(toX, toY);
        ctx.closePath();
        ctx.fill();
    }

