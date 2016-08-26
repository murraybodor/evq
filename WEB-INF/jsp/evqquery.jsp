<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<script lang="javascript">
 var req;
 
 /*
  *
  */
 function searchAndFilter(geogType, filterBox, searchBox){
    
    filterBoxElement = document.getElementById(filterBox);
    searchBoxElement = document.getElementById(searchBox);
    
	search = searchBoxElement.value;
	if (filterBoxElement.value.length>0) {
		filter = "(" + filterBoxElement.value + ")";
	} else {
		filter="";
	}	
	
    //get the (form based) params to push up as part of the get request
    url="evq.do?method=Filter&geog="+geogType+"&filter="+filter+"&search="+search;
  
    //Do the Ajax call
    if (window.XMLHttpRequest){ // Non-IE browsers
      req = new XMLHttpRequest();
      //A call-back function is define so the browser knows which function to call after the server gives a reponse back
	  if (geogType=="SB") {
	      req.onreadystatechange = populateSubBox;
	  } else {
	      req.onreadystatechange = populateMpBox;
	  }
	  
      try {
      	req.open("GET", url, true); //was get
      } catch (e) {
         alert("Cannot connect to server");
      }
      req.send(null);
    } else if (window.ActiveXObject) { // IE      
      req = new ActiveXObject("Microsoft.XMLHTTP");
      if (req) {
		  if (geogType=="SB") {
		      req.onreadystatechange = populateSubBox;
		  } else {
		      req.onreadystatechange = populateMpBox;
		  }
	      req.open("GET", url, true);
    	  req.send();
      }
    }
  }
  
  //Callback function
  function populateSubBox(){
  	document.getElementById('subBox').options.length = 0;

    if (req.readyState == 4) { // Complete
      if (req.status == 200) { // OK response
         textToSplit = req.responseText
         if(textToSplit == '803'){
			alert("No select option available on the server")
		  }
          //Split the document
          returnElements=textToSplit.split("||")
          
          //Process each of the elements 	
          for ( var i=0; i<returnElements.length; i++ ){
             valueLabelPair = returnElements[i].split("|")
             document.getElementById('subBox').options[i] = new Option(valueLabelPair[0], valueLabelPair[1]);
          }
        }
      } else {  
            // alert("Bad response by the server");
        }    
    }

  //Callback function
  function populateMpBox(){
  	document.getElementById('mpBox').options.length = 0;

    if (req.readyState == 4) { // Complete
      if (req.status == 200) { // OK response
         textToSplit = req.responseText
         if(textToSplit == '803'){
			alert("No select option available on the server")
		  }
          //Split the document
          returnElements=textToSplit.split("||")
          
          //Process each of the elements 	
          for ( var i=0; i<returnElements.length; i++ ){
             valueLabelPair = returnElements[i].split("|")
             document.getElementById('mpBox').options[i] = new Option(valueLabelPair[0], valueLabelPair[1]);
          }
        }
      } else {  
            // alert("Bad response by the server");
        }    
    }



 /*
  * Get the granularities by calling a Struts action
  */
 function retrieveGranularities(){
    
    granBox = document.getElementById('granBox');
    
    //Nothing selected
    if(granBox.selectedIndex==0){
      return;
    }
    selectedGran = granBox.options[granBox.selectedIndex].value;
    
    //get the (form based) params to push up as part of the get request
    url="evq.do?method=changeGran&gran="+selectedGran;
  
    //Do the Ajax call
    if (window.XMLHttpRequest){ // Non-IE browsers
      req = new XMLHttpRequest();
      //A call-back function is define so the browser knows which function to call after the server gives a reponse back
      req.onreadystatechange = populateCoincBox;
      try {
      	req.open("GET", url, true); //was get
      } catch (e) {
         alert("Cannot connect to server");
      }
      req.send(null);
    } else if (window.ActiveXObject) { // IE      
      req = new ActiveXObject("Microsoft.XMLHTTP");
      if (req) {
        req.onreadystatechange = populateCoincBox;
        req.open("GET", url, true);
        req.send();
      }
    }
  }
  
  //Callback function
  function populateCoincBox(){
  	document.getElementById('coincBox').options.length = 0;

    if (req.readyState == 4) { // Complete
      if (req.status == 200) { // OK response
         textToSplit = req.responseText
         if(textToSplit == '803'){
			alert("No select option available on the server")
		  }
          //Split the document
          returnElements=textToSplit.split("||")
          
          //Process each of the elements 	
          for ( var i=0; i<returnElements.length; i++ ){
             valueLabelPair = returnElements[i].split("|")
             document.getElementById('coincBox').options[i] = new Option(valueLabelPair[0], valueLabelPair[1]);
          }
          togglePoiRbs();
        }
      } else {  
            // alert("Bad response by the server");
        }    
    }

  function togglePoiRbs() {

    alert("value="+document.getElementById('coincBox').options[document.getElementById('coincBox').selectedIndex].value);
    
    if (document.getElementById('coincBox').options[document.getElementById('coincBox').selectedIndex].value == "DE") {
		document.getElementById('poiLoadRb').checked=true;
		document.getElementById('poiLoadRb').disabled=true;
		document.getElementById('poiGenRb').disabled=true;
    } else if (document.getElementById('coincBox').options[document.getElementById('coincBox').selectedIndex].value == "SU") {
		document.getElementById('poiGenRb').checked=true;
		document.getElementById('poiLoadRb').disabled=true;
		document.getElementById('poiGenRb').disabled=true;
    } else {
		document.getElementById('poiLoadRb').disabled=false;
		document.getElementById('poiGenRb').disabled=false;
    } 

 }    

 function addArea(){
	areaBox = document.getElementById('areaBox');
    
    //Nothing selected
    if(areaBox.selectedIndex==0){
      return;
    }
    
	for (loop=0; loop < areaBox.options.length; loop++)
  	{
  		if (areaBox.options[loop].selected) {
		    selectedArea = areaBox.options[loop].value;
			var addedArea = new Option(selectedArea, selectedArea);
		    document.getElementById('selectedAreaBox').options[document.getElementById('selectedAreaBox').length] = addedArea;
  		}
  	}    
    
	return false;
 }

 function removeArea(){
     document.getElementById('selectedAreaBox').options[document.getElementById('selectedAreaBox').selectedIndex] = null;
 }
    
 function addSub(){
	subBox = document.getElementById('subBox');
    
    //Nothing selected
    if(subBox.selectedIndex==0){
      return;
    }
    
	for (loop=0; loop < subBox.options.length; loop++)
  	{
  		if (subBox.options[loop].selected) {
		    selectedSub = subBox.options[loop].value;
			var addedSub = new Option(selectedSub, selectedSub);
		    document.getElementById('selectedSubBox').options[document.getElementById('selectedSubBox').length] = addedSub;
  		}
  	}    
    
	return false;
 }

 function removeSub(){
    document.getElementById('selectedSubBox').options[document.getElementById('selectedSubBox').selectedIndex] = null;
 }

 function addMp(){
	mpBox = document.getElementById('mpBox');
    
    //Nothing selected
    if(mpBox.selectedIndex==0){
      return;
    }
    
	for (loop=0; loop < mpBox.options.length; loop++)
  	{
  		if (mpBox.options[loop].selected) {
		    selectedMp = mpBox.options[loop].value;
			var addedMp = new Option(selectedMp, selectedMp);
		    document.getElementById('selectedMpBox').options[document.getElementById('selectedMpBox').length] = addedMp;
  		}
  	}    
  	
	return false;
 }

 function removeMp() {
    document.getElementById('selectedMpBox').options[document.getElementById('selectedMpBox').selectedIndex] = null;
 }
 
 function medianClick() {
    poiMedianCb = document.getElementById('poiMedianCb');
 }    
    
 function submit() {
 
 }
 
 
</script>
<html:form action="/evq.do?method=Submit">
<table>
<logic:messagesPresent>
<tr>
<td>
<font color="red"><bean:message key="error.header" /></font>
</td>
</tr>
 <html:messages id="error" message="true">
  <bean:write name="error" />
 </html:messages>
</ul>
</logic:messagesPresent>
	<tr class="namePanel" valign="middle">
		<td colspan="2" class="boldLabel">Enter a query name (optional): <html:text name="EvqActionForm" property="query.queryName" size="75"/></td>
	</tr>
	<tr class="queryDetailLeftRightPanel" valign="top">
		<td class="queryDetailLeftPanel" align="left">
			<table class="datePanel">
			<tr valign="top">
				<td align="left">
					<table cellpadding="0" cellspacing="0" class="dateTabPanel">
						<tr>
							<td class="titleLabel">Select Date</td>
						</tr>
						<tr valign="bottom">
							<td>
								<table cellpadding="0" cellspacing="0" class="gwt-TabBar">
									<tr valign="bottom">
										<logic:equal name="EvqActionForm" property="query.dateQueryType" value="SP">
											<td NOWRAP class="gwt-TabBarItem-selected">Specific Dates</td>
										</logic:equal>
										<logic:notEqual name="EvqActionForm" property="query.dateQueryType" value="SP">
											<td NOWRAP class="gwt-TabBarItem"><html:link action="/evq.do?method=date&dateType=SP">Specific Dates</html:link></td>
										</logic:notEqual>
										<logic:equal name="EvqActionForm" property="query.dateQueryType" value="CA">
											<td NOWRAP class="gwt-TabBarItem-selected">Calendar Years</td>
										</logic:equal>
										<logic:notEqual name="EvqActionForm" property="query.dateQueryType" value="CA">
											<td NOWRAP class="gwt-TabBarItem"><html:link action="/evq.do?method=date&dateType=CA">Calendar Years</html:link></td>
										</logic:notEqual>
										<logic:equal name="EvqActionForm" property="query.dateQueryType" value="TW">
											<td NOWRAP class="gwt-TabBarItem-selected">Two Season Years</td>
										</logic:equal>
										<logic:notEqual name="EvqActionForm" property="query.dateQueryType" value="TW">
											<td NOWRAP class="gwt-TabBarItem"><html:link action="/evq.do?method=date&dateType=TW">Two Season Years</html:link></td>
										</logic:notEqual>
										<logic:equal name="EvqActionForm" property="query.dateQueryType" value="FO">
											<td NOWRAP class="gwt-TabBarItem-selected">Four Season Years</td>
										</logic:equal>
										<logic:notEqual name="EvqActionForm" property="query.dateQueryType" value="FO">
											<td NOWRAP class="gwt-TabBarItem"><html:link action="/evq.do?method=date&dateType=FO">Four Season Years</html:link></td>
										</logic:notEqual>
									</tr>
								</table>
							</td>
						</tr>
						<tr valign="top">
							<td>
								<logic:equal name="EvqActionForm" property="query.dateQueryType" value="SP">
									<table class="specificDateTable">
									<tr valign="middle">
										<td width="15%" NOWRAP class="label">Begin Date:</td>
										<td width="15%" NOWRAP><html:text name="EvqActionForm" property="query.beginDate" size="11"/></td>
										<td width="15%" NOWRAP class="smallLabel">YYYY-MM-DD</td>
										<td NOWRAP>&nbsp;</td>
									</tr>
									<tr valign="middle">
										<td NOWRAP class="label">End Date:</td>
										<td NOWRAP><html:text name="EvqActionForm" property="query.endDate" size="11"/></td>
										<td NOWRAP>&nbsp;</td>
										<td NOWRAP>&nbsp;</td>
									</tr>
									</table>
								</logic:equal>
								<logic:equal name="EvqActionForm" property="query.dateQueryType" value="CA">
									<table class="calendarDateTable">
									<tr valign="middle">
										<td width="15%" NOWRAP class="label">Begin Year:</td>
										<td width="15%" NOWRAP>
											<html:select size="1" name="EvqActionForm" property="query.calBeginYear">
												<html:options name="EvqActionForm" property="years"/>
											</html:select>
										</td>
										<td NOWRAP>&nbsp;</td>
									</tr>
									<tr valign="middle">
										<td NOWRAP class="label">End Year:</td>
										<td NOWRAP>
											<html:select size="1" name="EvqActionForm" property="query.calEndYear">
												<html:options name="EvqActionForm" property="years"/>
											</html:select>
										</td>
										<td NOWRAP>&nbsp;</td>
									</tr>
									</table>
								</logic:equal>
								<logic:equal name="EvqActionForm" property="query.dateQueryType" value="TW">
									<table class="twoSeasonsDateTable">
									<tr valign="middle">
										<td width="15%" NOWRAP class="label">Begin Year:</td>
										<td width="15%" NOWRAP>
											<html:select size="1" name="EvqActionForm" property="query.twoSeasBeginYear">
												<html:options name="EvqActionForm" property="years"/>
											</html:select>
										</td>
										<td width="15%" NOWRAP class="label">
											<html:checkbox name="EvqActionForm" property="query.twoSeasonSummerSelected" value="true">Summer</html:checkbox>&nbsp;
										</td>
										<td NOWRAP>&nbsp;</td>
									</tr>
									<tr valign="middle">
										<td NOWRAP class="label">End Year:</td>
										<td NOWRAP>
											<html:select size="1" name="EvqActionForm" property="query.twoSeasEndYear">
												<html:options name="EvqActionForm" property="years"/>
											</html:select>
										</td>
										<td NOWRAP class="label">
											<html:checkbox name="EvqActionForm" property="query.twoSeasonWinterSelected" value="true">Winter</html:checkbox>&nbsp;
										</td>
										<td NOWRAP>&nbsp;</td>
									</tr>
									</table>
								</logic:equal>
								<logic:equal name="EvqActionForm" property="query.dateQueryType" value="FO">
									<table class="fourSeasonsDateTable">
									<tr valign="middle">
										<td width="15%" NOWRAP class="label">Begin Year:</td>
										<td width="15%" NOWRAP>
											<html:select size="1" name="EvqActionForm" property="query.fourSeasBeginYear">
												<html:options name="EvqActionForm" property="years"/>
											</html:select>
										</td>
										<td width="15%" NOWRAP class="label">
											<html:checkbox name="EvqActionForm" property="query.fourSeasonSpringSelected" value="true">Spring</html:checkbox>&nbsp;
										</td>
										<td width="15%" NOWRAP class="label">
											<html:checkbox name="EvqActionForm" property="query.fourSeasonFallSelected" value="true">Fall</html:checkbox>&nbsp;
										</td>
										<td NOWRAP>&nbsp;</td>
									</tr>
									<tr valign="middle">
										<td NOWRAP class="label">End Year:</td>
										<td NOWRAP class="label">
											<html:select size="1" name="EvqActionForm" property="query.fourSeasEndYear">
												<html:options name="EvqActionForm" property="years"/>
											</html:select>
										</td>
										<td NOWRAP class="label">
											<html:checkbox name="EvqActionForm" property="query.fourSeasonSummerSelected" value="true">Summer</html:checkbox>&nbsp;
										</td>
										<td NOWRAP class="label">
											<html:checkbox name="EvqActionForm" property="query.fourSeasonWinterSelected" value="true">Winter</html:checkbox>&nbsp;
										</td>
										<td NOWRAP>&nbsp;</td>
									</tr>
									</table>
								</logic:equal>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr valign="top">
				<td class="geogPanel" align="left">
					<table cellpadding="0" cellspacing="0" class="geogTabPanel">
						<tr valign="top">
							<td class="titleLabel">Select Geographic Area</td>
						</tr>
						<tr valign="bottom">
							<td>
								<table cellpadding="0" cellspacing="0" class="gwt-TabBar">
									<tr valign="bottom">
										<logic:equal name="EvqActionForm" property="query.geogQueryType" value="ES">
											<td NOWRAP class="gwt-TabBarItem-selected">Entire System</td>
										</logic:equal>
										<logic:notEqual name="EvqActionForm" property="query.geogQueryType" value="ES">
											<td NOWRAP class="gwt-TabBarItem"><html:link action="/evq.do?method=geog&geogType=ES">Entire System</html:link></td>
										</logic:notEqual>
										<logic:equal name="EvqActionForm" property="query.geogQueryType" value="RG">
											<td NOWRAP class="gwt-TabBarItem-selected">Region</td>
										</logic:equal>
										<logic:notEqual name="EvqActionForm" property="query.geogQueryType" value="RG">
											<td NOWRAP class="gwt-TabBarItem"><html:link action="/evq.do?method=geog&geogType=RG">Region</html:link></td>
										</logic:notEqual>
										<logic:equal name="EvqActionForm" property="query.geogQueryType" value="PA">
											<td NOWRAP class="gwt-TabBarItem-selected">Planning Area</td>
										</logic:equal>
										<logic:notEqual name="EvqActionForm" property="query.geogQueryType" value="PA">
											<td NOWRAP class="gwt-TabBarItem"><html:link action="/evq.do?method=geog&geogType=PA">Planning Area</html:link></td>
										</logic:notEqual>
										<logic:equal name="EvqActionForm" property="query.geogQueryType" value="SB">
											<td NOWRAP class="gwt-TabBarItem-selected">Substation</td>
										</logic:equal>
										<logic:notEqual name="EvqActionForm" property="query.geogQueryType" value="SB">
											<td NOWRAP class="gwt-TabBarItem"><html:link action="/evq.do?method=geog&geogType=SB">Substation</html:link></td>
										</logic:notEqual>
										<logic:equal name="EvqActionForm" property="query.geogQueryType" value="MP">
											<td NOWRAP class="gwt-TabBarItem-selected">Meas Point</td>
										</logic:equal>
										<logic:notEqual name="EvqActionForm" property="query.geogQueryType" value="MP">
											<td NOWRAP class="gwt-TabBarItem"><html:link action="/evq.do?method=geog&geogType=MP">Meas Point</html:link></td>
										</logic:notEqual>
										<logic:equal name="EvqActionForm" property="query.geogQueryType" value="IE">
											<td NOWRAP class="gwt-TabBarItem-selected">Import/Export</td>
										</logic:equal>
										<logic:notEqual name="EvqActionForm" property="query.geogQueryType" value="IE">
											<td NOWRAP class="gwt-TabBarItem"><html:link action="/evq.do?method=geog&geogType=IE">Import/Export</html:link></td>
										</logic:notEqual>
									</tr>
								</table>
							
							</td>
						</tr>
						<tr valign="top">
							<td>
								<logic:equal name="EvqActionForm" property="query.geogQueryType" value="ES">
									<table class="entireSystemGeogTable">
									<tr valign="middle">
										<td NOWRAP class="label">ENTIRE SYSTEM QUERY NOT AVAILABLE</td>
									</tr>
									</table>
								</logic:equal>
								<logic:equal name="EvqActionForm" property="query.geogQueryType" value="RG">
									<table class="regionGeogTable">
									<tr valign="top">
										<td NOWRAP class="label">REGION QUERY CURRENTLY NOT AVAILABLE</td>
									</tr>
									<tr valign="top">
										<td NOWRAP>
											<html:select disabled="true" multiple="true" size="10" name="EvqActionForm" property="query.regions">
												<html:optionsCollection name="EvqActionForm" property="regions" />
											</html:select>
										</td>
									</tr>
									</table>
								</logic:equal>
								<logic:equal name="EvqActionForm" property="query.geogQueryType" value="PA">
									<table class="areaGeogTable">
									<tr valign="top">
										<td NOWRAP class="label">Select one or more planning areas:</td>
										<td>&nbsp;</td>
										<td NOWRAP class="label">Currently Selected:</td>
									</tr>
									<tr valign="top">
										<td NOWRAP>
											<html:select styleClass="listBox" styleId="areaBox" multiple="true" size="12" name="EvqActionForm" property="nullList">
												<html:optionsCollection name="EvqActionForm" property="planningAreas" />
											</html:select>
										</td>
										<td NOWRAP align="left">
											<input type="button" value="Add -->" onclick="addArea();return;" /><br><br><br>
											<input type="button" value="Remove" onclick="removeArea();return;" />
										</td>
										<td NOWRAP>
											<html:select styleClass="smallListBox" styleId="selectedAreaBox" size="12" name="EvqActionForm" property="nullList">
												<html:options name="EvqActionForm" property="selectedAreas" />
											</html:select>
										</td>
									</tr>
									</table>
								</logic:equal>
								<logic:equal name="EvqActionForm" property="query.geogQueryType" value="SB">
									<table class="substationGeogTable">
									<tr valign="top">
										<td NOWRAP class="label">Select area filter (optional):</td>
										<td NOWRAP>&nbsp;</td>
										<td NOWRAP class="label">Currently selected:</td>
									</tr>
									<tr valign="top">
										<td NOWRAP>
											<html:select size="1" name="EvqActionForm" styleId="filterArea" property="filterArea" onchange="searchAndFilter('SB', 'filterArea', 'searchSub');">
												<html:option value=""></html:option>
												<html:optionsCollection name="EvqActionForm" property="planningAreas" />
											</html:select>
										</td>
										<td NOWRAP>&nbsp;</td>
										<td NOWRAP rowspan="5">
											<html:select styleClass="smallListBox" styleId="selectedSubBox" size="15" name="EvqActionForm" property="nullList">
												<html:options name="EvqActionForm" property="selectedSubs" />
											</html:select>
										</td>
									</tr>
									<tr valign="top">
										<td NOWRAP class="label">Search for substations:</td>
										<td NOWRAP>&nbsp;</td>
									</tr>
									<tr valign="top">
										<td NOWRAP class="label">
											<input type="text" name="searchSub" id="searchSub" size="12" onKeyUp="searchAndFilter('SB', 'filterArea', 'searchSub');">
										</td>
										<td NOWRAP>&nbsp;</td>
									</tr>
									<tr valign="top">
										<td NOWRAP rowspan=2>
											<html:select styleClass="listBox" styleId="subBox" multiple="true" size="10" name="EvqActionForm" property="nullList">
												<html:optionsCollection name="EvqActionForm" property="substations" />
											</html:select>
										</td>
										<td NOWRAP align="left">
											<input type="button" value="Add -->" onclick="addSub();return;" /><br><br><br>
											<input type="button" value="Remove" onclick="removeSub();return;" />
										</td>
									</tr>
									</table>
								</logic:equal>
								<logic:equal name="EvqActionForm" property="query.geogQueryType" value="MP">
									<table class="mpGeogTable">
									<tr valign="top">
										<td NOWRAP class="label">Select area filter (optional):</td>
										<td NOWRAP>&nbsp;</td>
										<td NOWRAP class="label">Currently selected:</td>
									</tr>
									<tr valign="top">
										<td NOWRAP>
											<html:select size="1" name="EvqActionForm" styleId="filterArea" property="filterArea" onchange="searchAndFilter('MP', 'filterArea', 'searchMp');">
												<html:option value=""></html:option>
												<html:optionsCollection name="EvqActionForm" property="planningAreas" />
											</html:select>
										</td>
										<td NOWRAP>&nbsp;</td>
										<td NOWRAP rowspan="5">
											<html:select styleClass="smallListBox" styleId="selectedMpBox" size="15" name="EvqActionForm" property="selectedMps">
												<html:options name="EvqActionForm" property="selectedMps" />
											</html:select>
										</td>
									</tr>
									<tr valign="top">
										<td NOWRAP class="label">Search for measurement points:</td>
										<td NOWRAP>&nbsp;</td>
									</tr>
									<tr valign="top">
										<td NOWRAP class="label">
											<input type="text" name="searchMp" id="searchMp" size="12" onKeyUp="searchAndFilter('MP', 'filterArea', 'searchMp');">
										</td>
										<td NOWRAP>&nbsp;</td>
									</tr>
									<tr valign="top">
										<td NOWRAP rowspan=2>
											<html:select styleClass="listBox" styleId="mpBox" multiple="true" size="10" name="EvqActionForm" property="nullList">
												<html:optionsCollection name="EvqActionForm" property="measurementPoints" />
											</html:select>
										</td>
										<td NOWRAP align="left">
											<input type="button" value="Add -->" onclick="addMp();return;" /><br><br><br>
											<input type="button" value="Remove" onclick="removeMp();return;" />
										</td>
									</tr>
									</table>
								</logic:equal>
								<logic:equal name="EvqActionForm" property="query.geogQueryType" value="IE">
									<table class="importExportGeogTable">
									<tr valign="middle">
										<td NOWRAP class="label">IMPORT/EXPORT QUERY NOT AVAILABLE</td>
									</tr>
									</table>
								</logic:equal>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			</table>
		</td>
		<td align="left">
			<table class="queryDetailRightPanel">
			<tr valign="top">
				<td class="volumesPanel" align="left">
					<table class="volumesSubPanel">
						<tr>
							<td colspan="2" class="titleLabel">Select Volume</td>
						</tr>
						<tr>
							<td class="label">Granularity:</td>
							<td NOWRAP>
								<html:select onchange="retrieveGranularities()" size="1" styleId="granBox" name="EvqActionForm" property="query.granularity">
									<html:optionsCollection name="EvqActionForm" property="granularities" />
								</html:select>
							</td>
						</tr>
						<tr>
							<td class="label">Category:</td>
							<td NOWRAP>
								<html:select size="1" name="EvqActionForm" property="query.category">
									<html:optionsCollection name="EvqActionForm" property="categories" />
								</html:select>
							</td>
						</tr>
						<tr>
							<td class="label">Type:</td>
							<logic:equal name="EvqActionForm" property="query.geogQueryType" value="MP">
							<td>&nbsp;</td>
							</logic:equal>
							<logic:notEqual name="EvqActionForm" property="query.geogQueryType" value="MP">
							<td NOWRAP class="label">
								<html:checkbox name="EvqActionForm" property="query.loadTypeSelected" value="true">Load</html:checkbox>&nbsp;
								<html:checkbox name="EvqActionForm" property="query.generationTypeSelected" value="true">Generation</html:checkbox>
							 </td>
							</logic:notEqual>
						</tr>
					</table>		
				</td>
			</tr>
			<tr valign="top">
				<td class="poiPanel" align="left">
					<table cellpadding="0" cellspacing="0" class="poiTabPanel">
						<tr>
							<td class="titleLabel">Select Interest Point</td>
						</tr>
						<tr valign="bottom">
							<td>
								<table cellpadding="0" cellspacing="0" class="gwt-TabBar">
									<tr valign="bottom">
										<logic:equal name="EvqActionForm" property="query.poiQueryType" value="PE">
											<td NOWRAP class="gwt-TabBarItem-selected">Interest Point</td>
										</logic:equal>
										<logic:notEqual name="EvqActionForm" property="query.poiQueryType" value="PE">
											<td NOWRAP class="gwt-TabBarItem"><html:link action="/evq.do?method=poi&poiType=PE">Interest Point</html:link></td>
										</logic:notEqual>
										<logic:equal name="EvqActionForm" property="query.poiQueryType" value="HO">
											<td NOWRAP class="gwt-TabBarItem-selected">Hourly</td>
										</logic:equal>
										<logic:notEqual name="EvqActionForm" property="query.poiQueryType" value="HO">
											<td NOWRAP class="gwt-TabBarItem"><html:link action="/evq.do?method=poi&poiType=HO">Hourly</html:link></td>
										</logic:notEqual>
									</tr>
								</table>
							</td>
						</tr>
						<tr valign="top">
							<td>
								<logic:equal name="EvqActionForm" property="query.poiQueryType" value="PE">
									<table class="poiPercentileTable">
									<tr>
										<td NOWRAP class="label">Coincident With:</td>
										<td NOWRAP>
											<html:select size="1" styleId="coincBox" onchange="togglePoiRbs();" name="EvqActionForm" property="query.poiCoincidence">
												<html:optionsCollection name="EvqActionForm" property="coincidences" />
											</html:select>
										</td>
									</tr>
									<tr>
										<td NOWRAP class="label">Category:</td>
										<td NOWRAP>
											<html:select size="1" name="EvqActionForm" property="query.poiCategory">
												<html:optionsCollection name="EvqActionForm" property="poiCategories" />
											</html:select>
										</td>
									</tr>
									<tr>
										<td NOWRAP class="label">Type:</td>
										<td class="label">
											<html:radio name="EvqActionForm" styleId="poiLoadRb" property="query.poiLoadSelected" disabled="true" value="true">Load</html:radio>
											<html:radio name="EvqActionForm" styleId="poiGenRb" property="query.poiLoadSelected" disabled="true" value="false">Generation</html:radio>
										 </td>
									</tr>
									<tr>
										<td NOWRAP class="label">Time Interval:</td>
										<td NOWRAP>
											<html:select size="1" name="EvqActionForm" property="query.timeInterval">
												<html:optionsCollection name="EvqActionForm" property="timeIntervals" />
											</html:select>
										</td>
									</tr>
									<tr>
										<td NOWRAP>&nbsp;</td>
										<td NOWRAP class="label">
											<html:checkbox name="EvqActionForm" property="query.poiPeakSelected" value="true">Peak</html:checkbox>&nbsp;
											<html:checkbox name="EvqActionForm" property="query.poiMedianSelected" styleId="poiMedianCb" onclick="medianClick();" value="true">Median</html:checkbox>&nbsp;
											<html:checkbox name="EvqActionForm" property="query.poiLightSelected" value="true">Light</html:checkbox>&nbsp;
										 </td>
									</tr>
									<tr>
										<td NOWRAP colspan="2" id="medianNote" class="smallLabel">NOTE: query duration may increase substantially for MEDIAN point</td>
									</tr>
									</table>
								</logic:equal>
								<logic:equal name="EvqActionForm" property="query.poiQueryType" value="HO">
									<table class="poiHourlyTable">
									<tr valign="middle">
										<td NOWRAP class="label">Query will return hourly values</td>
									</tr>
									</table>
								</logic:equal>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			</table>
		
		</td>
	</tr>	
	<tr>
		<td colspan="2" align="center" class="buttonPanel">
		<html:submit> 
          <bean:message key="button.reset"/> 
        </html:submit>&nbsp;
        <html:submit> 
          <bean:message key="button.submit"/> 
        </html:submit> 
		</td>
	</tr>
</table>
</html:form>