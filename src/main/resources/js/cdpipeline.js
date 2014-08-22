// Show the changes/completions in list view
function showListView(list, table) {
	document.getElementById(table).style.display = "none";
	document.getElementById(list).style.display = "block";
 	document.getElementsByClassName("list-view-button")[0].disabled = true;
 	document.getElementsByClassName("table-view-button")[0].disabled = false;
 	document.getElementsByClassName("select-table-button")[0].style.display = "none";
 	document.getElementsByClassName("select-table-button")[0].disabled = true;	
}

// Show the changes/completions in table view
function showTableView(table, list) {
	document.getElementById(table).style.display = "table";
	document.getElementById(list).style.display = "none";
 	document.getElementsByClassName("list-view-button")[0].disabled = false;
 	document.getElementsByClassName("table-view-button")[0].disabled = true;
 	document.getElementsByClassName("select-table-button")[0].style.display = "block";
 	document.getElementsByClassName("select-table-button")[0].disabled = false;	
}

// Select the changes/completions table
function selectTable(el) {
    var body = document.body, range, sel;
    if (document.createRange && window.getSelection) {
       	range = document.createRange();
        sel = window.getSelection();
        sel.removeAllRanges();
        try {
            range.selectNodeContents(el);
            sel.addRange(range);
        } catch (e) {
           	 range.selectNode(el);
            sel.addRange(range);
        }
    } else if (body.createTextRange) {
        range = body.createTextRange();
        range.moveToElementText(el);
        range.select();
    }
}


// link a build number to its corresponding page on Bamboo
function showBuildPage(buildNumber) {
	// set the url of iframe
	document.getElementById("buildPage").src = document.getElementById("baseUrl").innerHTML + '-' + buildNumber;
	document.getElementById("buildPage").style.display = "block";
	document.getElementById("completions-window").style.display = "none";
	document.getElementById("return-comp-button").style.display = "block";
}

// return from a build page on Bamboo to the list of completions
function returnToCompletions() {
	// reset the url of iframe
	document.getElementById("buildPage").src = '';

	document.getElementById("buildPage").style.display = "none";
	document.getElementById("completions-window").style.display = "block";
	document.getElementById("return-comp-button").style.display = "none";
}
