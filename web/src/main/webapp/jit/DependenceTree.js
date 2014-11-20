function createTree(injectInto,orientation){
	//init Spacetree
    //Create a new ST instance
    var st = new $jit.ST({
        //id of viz container element
        injectInto: injectInto,
        
        orientation: orientation,
        //set duration for the animation
        duration: 100,
        //set animation transition type
        transition: $jit.Trans.Quart.easeInOut,
        //set distance between node and its children
        levelDistance: 50,
        //enable panning
        Navigation: {
          enable:true,
          panning:true
        },	
        //set node and edge styles
        //set overridable=true for styling individual
        //nodes or edges
        Node: {
            height: 40,
            width: 100,
            type: 'rectangle',
            color: '#aaa',
            overridable: true
        },
        
        Edge: {
            type: 'bezier',
            dim: 8,
            overridable: true
        },
        
        
        //This method is called on DOM label creation.
        //Use this method to add event handlers and styles to
        //your node.
        onCreateLabel: function(label, node){
            label.id = node.id;            
            label.innerHTML ="("+node.data.jobId+")<br>"+node.name;
			
            label.onclick = function(){
            	  st.onClick(node.id);
            	
            };
			label.onmouseover=function(){
				//console.log('over'+node.id);
			}
            //set label styles
            var style = label.style;
            //style.width = 60 + 'px';
            style.height = 17 + 'px';      
			style.width='100px';
			style.cursor = 'pointer';
			style.color='black';
            style.fontSize = '0.8em';
            style.textAlign= 'center';
            style.paddingTop = '3px';
        },
        
        //This method is called right before plotting
        //a node. It's useful for changing an individual node
        //style properties before plotting it.
        //The data properties prefixed with a dollar
        //sign will override the global node style properties.
        onBeforePlotNode: function(node){
            //add some color to the nodes in the path between the
            //root node and the selected node.
        	var time=new Date(node.data.lastRuntime);
        	var now=new Date();
        	if(time.getFullYear()==now.getFullYear() && 
        			time.getMonth()==now.getMonth() &&
        			time.getDay()==now.getDay()){
        		if(node.data.lastStatus.toLowerCase()=='success'){
    				node.data.$color='#00CACA';
    			}else if(node.data.lastStatus.toLowerCase()=='running'){
    				node.data.$color='#00EC00';
    			}else if(node.data.lastStatus.toLowerCase()=='failed'){
    				node.data.$color='red';
    			}else {
    				node.data.$color='gray';
    			}
        	}else{
        		node.data.$color='gray';
        	}
			return;
        },
        
        //This method is called right before plotting
        //an edge. It's useful for changing an individual edge
        //style properties before plotting it.
        //Edge data proprties prefixed with a dollar sign will
        //override the Edge global style properties.
        onBeforePlotLine: function(adj){
            if (adj.nodeFrom.selected && adj.nodeTo.selected) {
                adj.data.$color = "#A23400";
                adj.data.$lineWidth = 3;
            }
            else {
                delete adj.data.$color;
                delete adj.data.$lineWidth;
            }
        }
    });

    return st;

}

