dojo.provide("azp.BBCodeEditor");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.Textarea");
dojo.require("dijit.form.SimpleTextarea");
dojo.require("dijit.Tooltip");

dojo.declare("azp.BBCodeEditor",
	[dijit._Widget, dijit._Templated],
	{	
		name: "",
		rows: 0,		
		cols: 20,
		value: "",
		style: "",
		areaType: "dijit.form.Textarea",
		widgetsInTemplate: true,
		
		templateString: "<div><span><button dojoType=\"dijit.form.Button\" showLabel=\"false\" iconClass=\"dijitEditorIcon dijitEditorIconBold\" dojoAttachEvent=\"onClick: bClick\" id=\"bBtn\"></button><span dojoType=\"dijit.Tooltip\" connectid=\"bBtn\"><b>Bold</b></span>"	
			+ "<button dojoType=\"dijit.form.Button\" showLabel=\"false\" iconClass=\"dijitEditorIcon dijitEditorIconItalic\" dojoAttachEvent=\"onClick: iClick\" id=\"iBtn\"></button><span dojoType=\"dijit.Tooltip\" connectid=\"iBtn\"><i>Italic</i></span>"
			+ "<button dojoType=\"dijit.form.Button\" showLabel=\"false\" iconClass=\"dijitEditorIcon dijitEditorIconUnderline\" dojoAttachEvent=\"onClick: uClick\" id=\"uBtn\"></button><span dojoType=\"dijit.Tooltip\" connectid=\"uBtn\"><u>Underline</u></span>"
			+ "<button dojoType=\"dijit.form.Button\" showLabel=\"false\" iconClass=\"dijitEditorIcon dijitEditorIconStrikethrough\" dojoAttachEvent=\"onClick: sClick\" id=\"sBtn\"></button><span dojoType=\"dijit.Tooltip\" connectid=\"sBtn\"><s>Strikethrough</s></span>"
			+ "<button dojoType=\"dijit.form.Button\" showLabel=\"false\" iconClass=\"dijitEditorIcon dijitEditorIconSuperscript\" dojoAttachEvent=\"onClick: supClick\" id=\"supBtn\"></button><span dojoType=\"dijit.Tooltip\" connectid=\"supBtn\">Super<sup>script</sup></span>"
			+ "<button dojoType=\"dijit.form.Button\" showLabel=\"false\" iconClass=\"dijitEditorIcon dijitEditorIconSubscript\" dojoAttachEvent=\"onClick: subClick\" id=\"subBtn\"></button><span dojoType=\"dijit.Tooltip\" connectid=\"subBtn\">Sub<sub>script</sub></span>"
			+ "<button dojoType=\"dijit.form.Button\" showLabel=\"false\" iconClass=\"dijitEditorIcon dijitEditorIconSelectAll\" dojoAttachEvent=\"onClick: quoteClick\" id=\"quoteBtn\"></button><span dojoType=\"dijit.Tooltip\" connectid=\"quoteBtn\">Quote</span>"
			+ "</span><br><textarea name=\"${name}\" dojoType=\"${areaType}\" rows=\"${rows}\" cols=\"${cols}\" style=\"${style}\" dojoAttachPoint=\"textArea\">${value}</textarea></div>",		


		//maps the buttons to their tags
		bClick: function(evt){ this.handleButton('b'); },		
		iClick: function(evt){ this.handleButton('i'); },
		uClick: function(evt){ this.handleButton('u'); },
		sClick: function(evt){ this.handleButton('s'); },
		supClick: function(evt){ this.handleButton('sup'); },
		subClick: function(evt){ this.handleButton('sub'); },
		quoteClick: function(evt){ this.handleButton('quote'); },
		
		//insert the tags
		handleButton: function(code){
			var tag1 = '[' + code + ']';
			var tag2 = '[/' + code + ']';
			var tan = this.textArea.domNode; 
			
			// Code for IE
			if (document.selection)	{
				tan.focus();
				var sel = document.selection.createRange();
				sel.text = tag1 + sel.text + tag2;
			}
		    else{  // Code for Real browsers :)
				var len = tan.value.length;
			    var start = tan.selectionStart;
				var end = tan.selectionEnd;
				
				
				var scrollTop = tan.scrollTop;
				var scrollLeft = tan.scrollLeft;

				
		        var sel = tan.value.substring(start, end);
			    var rep = tag1 + sel + tag2;
				tan.value =  tan.value.substring(0,start) + rep + tan.value.substring(end,len);
				
				tan.scrollTop = scrollTop;
				tan.scrollLeft = scrollLeft;				
				
			}
			
			
		},		
		
		postMixInProperties : function(){
			// If rows is spsecified, don't use the flexible textarea
			if(this.rows > 0){
				this.areaType = "dijit.form.SimpleTextarea";
			}			
			
			// this is to get the value between the textarea tags 
			// when the BBCodeEditor is added to a page in markup 
			if(this.srcNodeRef && this.srcNodeRef.value){
				this.value = this.srcNodeRef.value;
			}
		},
		
		getValue: function(){
			return this.textArea.getValue();			
		},
		
		setValue: function(text){
			this.textArea.setValue(text);
		}
		

	}
);


