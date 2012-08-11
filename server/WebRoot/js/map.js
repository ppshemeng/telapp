var map = new BMap.Map("container");            // ����Mapʵ��
var point = new BMap.Point(108.914518, 34.235076);    // ����������
var circle;
var currentmarker;
addCurrentMarker(point);
openMapSetting(point);
addMenu();

//��ȡ������Ϣ
$.post("shop.jsp", { latitude: 34.2347831, longitude: 108.9130483 } ,
function(data)
{
	addshops(data);
}); 

//�������󷵻����������ڵ�ͼ�����Ӳ���ͼ��
function addshops(data)
{
	var json = eval('(' + data + ')'); 
	var content = "";
	for (i = 0; i < json.length; i++)
	{
		var longitude = parseFloat(json[i].shoplng);
		var latitude = parseFloat(json[i].shoplat);	
		var pt = new BMap.Point(longitude, latitude);
		var sContent = 
		"<div width:200;height:200px;border:1px><a target='_blank' href='shopmenu.jsp?shopid=" + json[i].shopid +"'>" +
		"<img style='float:right;margin:4px' id='imgDemo' src='img/logo.gif' " + 
		" width='100' height='80' title='" + json[i].shopname + "'/></a>" + 
		"<p style='margin:0;line-height:1.5;font-size:13px;text-indent:2em'>����: " + 
		json[i].shopname +"�绰: " + json[i].shopphone +"����: " + json[i].shopdistance +"��</p>" + 
		"</div>";
		
		var infoWindow = new BMap.InfoWindow(sContent);  // ������Ϣ���ڶ���
		addMarker(pt, infoWindow);
		content += sContent + "<br/>";
	}
	//�ı���ʾ
	document.getElementById("content").innerHTML = content;
}
function trim(str){return   str.replace(/^\s+|\s+$/,   '')}
// ��д�Զ��庯��,������ע
function addMarker(point, infoWindow){
  var marker = new BMap.Marker(point);
  map.addOverlay(marker);
  marker.addEventListener("click", function(){          
   this.openInfoWindow(infoWindow);
   //ͼƬ��������ػ�infowindow
   document.getElementById('imgDemo').onload = function (){
       infoWindow.redraw();
   }
});
}
//�����ҵ�λ��  ��ע��ƽ�Ƶ���λ��
function addCurrentMarker(point)
{
	//����С����
	var myIcon = new BMap.Icon("http://dev.baidu.com/wiki/static/map/API/img/fox.gif", new BMap.Size(300,157));
  currentmarker = new BMap.Marker(point,{icon:myIcon});  // ������ע
	map.addOverlay(currentmarker);              // ����ע��ӵ���ͼ��

	//��С����˵����������Ϣ���ڣ�
	//var infoWindow = new BMap.InfoWindow("<p style='font-size:14px;'>��������!</p>");
	//currentmarker.addEventListener("click", function(){this.openInfoWindow(infoWindow);});

	addOverlay(point, 1000);
	map.panTo(point);
}

// ��д����������
function addOverlay(point, radius)
{
	circle = new BMap.Circle(point,radius);
	map.addOverlay(circle);
}

// ����map������
function openMapSetting(point)
{
	map.centerAndZoom(point,15);                     // ��ʼ����ͼ,�������ĵ�����͵�ͼ����
	map.enableScrollWheelZoom();  //��������             
	map.addControl(new BMap.ScaleControl());                    // ��ӱ����߿ؼ�
	var opts = {anchor: BMAP_ANCHOR_TOP_RIGHT, offset: new BMap.Size(10, 10)}; 
	map.addControl(new BMap.NavigationControl(opts));              //������Ե�ͼ�ؼ�
}
//����Ҽ��˵�
function addMenu()
{
	var contextMenu = new BMap.ContextMenu();
	var txtMenuItem = [
		{
	   text:'�Ŵ�',
	   callback:function(){map.zoomIn()}
	  },
	  {
	   text:'��С',
	   callback:function(){map.zoomOut()}
	  },
	  {
	   text:'�������λ��',
	   callback:function(p){
	    var marker = new BMap.Marker(p), px = map.pointToPixel(p);
	    map.clearOverlays();
	    //��ȡ������Ϣ
			$.post("shop.jsp", { latitude: p.lat, longitude: p.lng } ,
			function(data)
			{
				addshops(data);
			}); 
	    point = new BMap.Point(p.lng, p.lat);
	    addCurrentMarker(point);
	   }
	  }
	 ];
	 for(var i=0; i < txtMenuItem.length; i++){
	  contextMenu.addItem(new BMap.MenuItem(txtMenuItem[i].text,txtMenuItem[i].callback,100));
	 }
 	map.addContextMenu(contextMenu);
}