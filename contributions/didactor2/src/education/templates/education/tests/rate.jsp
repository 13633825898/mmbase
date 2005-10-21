<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>

<%@page import="java.util.Iterator"%>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">



<mm:import externid="tests" required="true"/>
<mm:import externid="learnobject" required="true"/>
<mm:import externid="thismadetest" required="true"/>
<mm:import externid="questionsshowed" jspvar="questionsShowed" required="true"/>
<mm:import externid="pagecounter" jspvar="pageCounter" vartype="Integer"/>
<mm:import externid="questionamount" jspvar="questionAmount" vartype="Integer"/>



<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>



<mm:node number="$tests" id="my_tests">

  <%-- Only the first time a madetests object is created --%>
  <mm:compare referid="thismadetest" value="">

    <%-- Save testresults --%>
    <mm:createnode type="madetests" id="madetest">
      <% long currentDate = System.currentTimeMillis() / 1000; %>
      <mm:setfield name="date"><%=currentDate%></mm:setfield>
      <mm:setfield name="score"><mm:write referid="TESTSCORE_INCOMPLETE"/></mm:setfield>
    </mm:createnode>

    <mm:createrelation role="related" source="my_tests" destination="madetest"/>



   <%// Make relation between copybooks instance and the madetest %>
   <%// Direct relation people->classrel->education %>
   <mm:compare referid="class" value="null">
      <mm:node number="$user">
         <mm:relatedcontainer path="classrel,educations">
            <mm:constraint field="educations.number" value="$education"/>
            <mm:related>
               <mm:node element="classrel" jspvar="tmp">
                  <%
                     System.out.println("classrel=" + tmp.getNumber());
                  %>
                  <mm:relatednodes type="copybooks" id="copybookID">
                  </mm:relatednodes>

                  <mm:write referid="copybookID" jspvar="tmp2" vartype="String">
                     <%
                        System.out.println("copybookID=" + tmp2);
                     %>
                  </mm:write>
               </mm:node>
            </mm:related>
         </mm:relatedcontainer>
      </mm:node>
   </mm:compare>

   <%// people->classrel->class->related->education %>
   <mm:compare referid="class" value="null" inverse="true">
      <mm:node number="$user">
         <mm:relatedcontainer path="classrel,classes">
            <mm:constraint field="classes.number" value="$class"/>
            <mm:related>
               <mm:node element="classrel">
                  <mm:relatednodes type="copybooks" id="copybookID">
                  </mm:relatednodes>
               </mm:node>
            </mm:related>
         </mm:relatedcontainer>
      </mm:node>
   </mm:compare>


    <mm:relatednodescontainer path="madetests,copybooks" element="madetests">

        <mm:constraint field="copybooks.number" referid="copybookID"/>

        <mm:relatednodes>

            <mm:relatednodescontainer type="givenanswers">

                <%--Remove Made test with  <mm:size/> answers<br/> --%>

                <mm:relatednodes>

                    <mm:maydelete>

                        <mm:deletenode deleterelations="true"/>

                    </mm:maydelete>

                </mm:relatednodes>

            </mm:relatednodescontainer>

            <mm:maydelete>

                <mm:deletenode deleterelations="true"/>

            </mm:maydelete>

        </mm:relatednodes>

    </mm:relatednodescontainer>



    <%-- Make relation between copybooks instance and the madetest --%>
    <mm:node number="$user">
       <mm:compare referid="class" value="null">
          <mm:relatedcontainer path="classrel,educations">
             <mm:constraint field="educations.number" value="$education"/>
             <mm:related>
                <mm:node element="classrel">
                   <mm:relatednodes type="copybooks" id="my_copybook">
                      <mm:createrelation role="related" source="my_copybook" destination="madetest"/>
                   </mm:relatednodes>
                </mm:node>
             </mm:related>
          </mm:relatedcontainer>
       </mm:compare>
       <mm:compare referid="class" value="null" inverse="true">
          <mm:relatedcontainer path="classrel,classes">
             <mm:constraint field="classes.number" value="$class"/>
             <mm:related>
                <mm:node element="classrel">
                   <mm:relatednodes type="copybooks" id="my_copybook">
                      <mm:createrelation role="related" source="my_copybook" destination="madetest"/>
                   </mm:relatednodes>
                </mm:node>
             </mm:related>
          </mm:relatedcontainer>
       </mm:compare>
    </mm:node>



  </mm:compare>



  <%-- Reuse the madetests object --%>

  <mm:compare referid="thismadetest" value="" inverse="true">

    <mm:node number="$thismadetest" id="madetest"/>

  </mm:compare>





  <%-- build list of all shown questions until now --%>

  <mm:import id="list" jspvar="list" vartype="List"><mm:write referid="questionsshowed"/></mm:import>



  <%



    //

    // iterate over the shown questions in order, so we

    // create the givenanswers objects in the given order too...

    //

    // this is needed because otherwise there is no way to determine

    // the order in which the questions were answered after this point!

    //

    Iterator i = list.iterator();

    while (i.hasNext()) {

        String qNumber = ((String) i.next()).replaceAll("_","");

        %>

  <%-- Examine different questions and save the given answers --%>

  <mm:node number="<%= qNumber %>">



    <%-- Which questions have been answered --%>

    <mm:import id="question" reset="true">shown<mm:field name="number"/></mm:import>

    <mm:import externid="$question" id="shownquestion" reset="true"/>



    <mm:import id="possiblequestion" reset="true"><mm:field name="number"/></mm:import>

    <%-- Only rate the answered question --%>



    <mm:compare referid="shownquestion" referid2="possiblequestion">



      <mm:relatednodescontainer path="givenanswers,madetests" element="givenanswers">

        <mm:constraint field="madetests.number" referid="madetest"/>

        <mm:relatednodes>

          <%-- Already an answer given to this question --%>

          <mm:deletenode deleterelations="true"/>

        </mm:relatednodes>

      </mm:relatednodescontainer>



      <mm:import id="page" reset="true">/education/<mm:nodeinfo type="type"/>/rate<mm:nodeinfo type="type"/>.jsp</mm:import>

      <mm:treeinclude page="$page" objectlist="$includePath" referids="$referids">

        <mm:param name="question"><mm:field name="number"/></mm:param>

        <mm:param name="madetest"><mm:write referid="madetest"/></mm:param>

      </mm:treeinclude>

    </mm:compare>



    <mm:remove referid="possiblequestion"/>

    <mm:remove referid="page"/>

  </mm:node>

  <% } %>







  <%-- If all questions are answerd then show the feedback else show next question set --%>

  <%

     if ( list.size() == questionAmount.intValue() ) {

  %>

       <mm:treeinclude page="/education/tests/totalscore.jsp"  objectlist="$includePath" referids="$referids">

         <mm:param name="madetest"><mm:write referid="madetest"/></mm:param>

         <mm:param name="tests"><mm:write referid="tests"/></mm:param>

       </mm:treeinclude>



       <mm:import id="page">/education/tests/feedback.jsp</mm:import>

       <mm:treeinclude page="$page" objectlist="$includePath" referids="$referids">

         <mm:param name="tests"><mm:field name="number"/></mm:param>

         <mm:param name="madetest"><mm:write referid="madetest"/></mm:param>

       </mm:treeinclude>

   <%

     } else {

   %>

         <mm:treeinclude page="/education/tests/buildtest.jsp"  objectlist="$includePath" referids="$referids">

           <mm:param name="learnobject"><mm:write referid="learnobject"/></mm:param>

           <mm:param name="madetest"><mm:write referid="madetest"/></mm:param>

           <mm:param name="questionsshowed"><mm:write referid="questionsshowed"/></mm:param>

           <mm:param name="pagecounter"><mm:write referid="pagecounter"/></mm:param>

         </mm:treeinclude>

   <%

     }

   %>



</mm:node>

</mm:cloud>
</mm:content>
