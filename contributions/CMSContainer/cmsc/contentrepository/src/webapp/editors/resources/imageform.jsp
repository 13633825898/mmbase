<html:hidden property="contenttypes" value="images" />
<table border="0">
   <tr>
      <td style="width: 150px"><fmt:message key="imageform.title" /></td>
      <td><html:text style="width: 250px" property="title"/></td>
   </tr>
   <tr>
      <td><fmt:message key="imageform.description" /></td>
      <td><html:text style="width: 250px" property="description"/></td>
   </tr>
   <tr>
      <td></td>
      <td><input type="submit" name="submitButton" onclick="setOffset(0);" 
      			value="<fmt:message key="imageform.submit" />"/></td>
   </tr>
</table>