<tr><td><b>Status Lists</b></td>
<td> <center>
Select one or more <a href="op_lhelp.html">status list</a><br>
<form METHOD=POST ACTION="/cgi-bin/op/op_status_list.cgi" >
<table class="hdr">
<tr>
<td> <table class="noborder" >
<tr>
<td><input type="checkbox" name="list_sched">Scheduled</td>
<td><input type="checkbox" name="list_ap">AP/no L2</td>
<td><input type="checkbox" name="list_ingest">Awaiting Ingest</td>
</tr>
<tr>
<td><input type="checkbox" name="list_vv">Awaiting V&amp;V</td>
<td><input type="checkbox" name="list_cdo">Awaiting CDO Review</td>
<td><input type="checkbox" name="list_aspect">Awaiting Aspect Review</td>
</tr>
<tr>
<td><input type="checkbox" name="list_dd">Awaiting Distribution</td>
<td><input type="checkbox" name="list_sap">SAP</td>
<td>&nbsp;</td>
</tr>
</table>
</td>
</tr>
<tr>
<td>Order by one or more:&nbsp;&nbsp;<font size=-1>(default is ObsId)</font><br>
<select name="order_one">
<option>ObsId<option>ObsDate<option>L2 AP Date
<option>V&amp;V Status<option>V&amp;V Date<option>Type<option>Sched Date
</select>
<select name="order_two">
<option selected>None<option>ObsId<option>ObsDate<option>L2 AP Date
<option>V&amp;V Status<option>V&amp;V Date<option>Type<option>Sched Date
</select></td>
<td><input type="submit" value="Go"></td>
</tr>
</table>
</form>
</center> 
</td></tr>
