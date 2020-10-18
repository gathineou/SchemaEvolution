var cy = window.cy = cytoscape({
  container: document.getElementById('visualize'),

  boxSelectionEnabled: false,
  autounselectify: true,

  layout: {
    name: 'dagre'
  },

  
  style: cytoscape.stylesheet()
    .selector('node')
    .css({
        'shape' : 'circle',
        'content': 'data(id)',
        'text-opacity': 0.5,
        'text-valign': 'bottom',
        'text-halign': 'bottom',
	      'font-size' : '6px',
        'background-color': '#10457D',
        'color' : 'black',
        'font-weight': '800',
        'font-family' : 'Helvetica'
    })
    .selector('.select')
    .css({
        'border-width': 3,
        'border-color': '#333',
    })
    .selector('edge')
    .css({
        'curve-style': 'bezier',
        'width': 4,
        'target-arrow-shape': 'triangle',
        'line-color': '#9dbaea',
        'target-arrow-color': '#9dbaea'
     })
});

cy.on('mouseover','node', function(e){
  console.log( 'hover ' + this.id() );
  cy.elements().removeClass('select');
  document.getElementById("nodeInfo").innerText = '';
  this.addClass('select');  
  document.getElementById("nodeInfo").innerText = this.json()["data"]["name"];
});

cy.on('click', 'node', function(e){
  console.log( 'clicked2 ' + this.id() );
  cy.elements().removeClass('select');
  document.getElementById("nodeInfo").innerText = '';
  this.addClass('select');
  document.getElementById("nodeInfo").innerText = this.json()["data"]["name"];
});

cy.on('mouseout','node', function(e) {
  cy.elements().removeClass('select');
  //document.getElementById("nodeInfo").innerText = globalResponse[globalId];
  if(globalQuery == "why"){
    document.getElementById("nodeInfo").innerText = JsonSchemaHover(globalResponse[globalId]);
  }
  else{
    document.getElementById("nodeInfo").innerText = globalResponse[globalId]["commandHover"];
  }
  

});

cy.on('tap', function(e) {
  if (e.cyTarget === cy) {
      cy.elements().removeClass('select');
      document.getElementById("nodeInfo").innerText = '';
  }
});
