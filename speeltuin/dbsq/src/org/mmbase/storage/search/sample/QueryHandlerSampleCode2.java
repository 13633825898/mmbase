package org.mmbase.storage.search.sample;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.support.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.implementation.database.*;

/**
 * Sample code demonstrating basic usage of the 
 * {@link org.mmbase.storage.search.SearchQueryHandler SearchQueryHandler}
 * interface.
 * <p>
 * Requires the following builders to be active: <code>builders</code> and 
 * <code>pools</code>.
 *
 * @author  Rob van Maris
 * @version $Revision: 1.1 $
 * @since MMBase-1.7
 */
public class QueryHandlerSampleCode2 {
    
    /**
     * Demo application.
     * <br>
     * Requires one commandline argument: the path to the MMBase config directory.
     * @param args Commandline arguments. 
     */
    public static void main(String args[]) throws Exception {
        if (args.length < 1) {
            System.out.println(
            "Requires one commandline argument: the path to the MMBase config directory.");
            System.exit(1);
        }
        MMBaseContext.init(args[0], true);
        
        // Sql handler that generates SQL strings.
        SqlHandler sqlHandler 
            = new BasicSqlHandler(new java.util.HashMap());
        
        MMBase mmbase = MMBase.getMMBase();
        MMJdbc2NodeInterface database = mmbase.getDatabase();
        MMObjectBuilder pools = mmbase.getBuilder("pools");
        MMObjectBuilder images = mmbase.getBuilder("images");
        InsRel insrel = mmbase.getInsRel();
        
        // EXAMPLE 1: query retrieving real nodes (of type pools).
        BasicSearchQuery query1 = new BasicSearchQuery();
        BasicStep step1a = query1.addStep(pools);
        // Add all persistent pools fields to the query.
        Iterator iFields1 = pools.getFields().iterator();
        while (iFields1.hasNext()) {
            FieldDefs field = (FieldDefs) iFields1.next();
            if (field.getDBState() == FieldDefs.DBSTATE_PERSISTENT
                    || field.getDBState() == FieldDefs.DBSTATE_SYSTEM) {
                query1.addField(step1a, field);
            }
        }
        
/* 
 Query equivalent to:
        SELECT * FROM <basename>_pools pools
 Returns: 
    real nodes with these fields: number, otype, owner, name, description      
 */
        System.out.println("Query: " + sqlHandler.toSql(query1, sqlHandler));
        
        // Execute, get result as real nodes.
        List nodes1 = database.getNodes(query1, pools);
        Iterator iNodes1 = nodes1.iterator();
        while (iNodes1.hasNext()) {
            MMObjectNode node = (MMObjectNode) iNodes1.next();
            System.out.println("Real node: " + node);
        }
        
        // EXAMPLE 2: query retrieving clusternodes.
        BasicSearchQuery query2 = new BasicSearchQuery();
        BasicStep step2a = query2.addStep(pools);
        BasicRelationStep step2b = query2.addRelationStep(insrel, images);
        step2b.setDirectionality(RelationStep.DIRECTIONS_DESTINATION);
        Step step2c = step2b.getNext();
        // Add at least the number fields of all steps.
        query2.addField(step2a, pools.getField("number"));
        query2.addField(step2b, insrel.getField("number"));
        query2.addField(step2c, images.getField("number"));
        
/*
 Query:
        SELECT 
            pools.number as number, 
            insrel.number as number, 
            images.number as number
        FROM 
            <basename>_pools pools, 
            <basename>_insrel insrel, 
            <baename>_images images
        WHERE (pools.number = insrel.snumber
        AND images.number = insrel.dnumber)
 */
        System.out.println("Query: " + sqlHandler.toSql(query2, sqlHandler));

        // Execute, result as clusternodes.
        List nodes2 = database.getNodes(query2, new ClusterBuilder(mmbase));
        Iterator iNodes2 = nodes2.iterator();
        while (iNodes2.hasNext()) {
            ClusterNode node = (ClusterNode) iNodes2.next();
            System.out.println("Clusternode: " + node);
        }

/*
 Returns: 
        clusternodes with these fields: pools.number, insrel.number, images.number      
 */ 

        // EXAMPLE 3: query retrieving clusternodes using table aliases.
        BasicSearchQuery query3 = new BasicSearchQuery();
        BasicStep step3a = query3.addStep(pools);
        BasicRelationStep step3b = query3.addRelationStep(insrel, images);
        step3b.setDirectionality(RelationStep.DIRECTIONS_DESTINATION);
        BasicStep step3c = (BasicStep) step3b.getNext();
        // Set table aliases.
        step3a.setAlias("pools0");
        step3c.setAlias("images3");
        // Add at least the number fields of all steps.
        query3.addField(step3a, pools.getField("number"));
        query3.addField(step3b, insrel.getField("number"));
        query3.addField(step3c, images.getField("number"));
        
/*
 Query:
        SELECT 
            pools0.number as number, 
            insrel.number as number, 
            images3.number as number
        FROM 
            <basename>_pools pools0, 
            <basename>_insrel insrel, 
            <baename>_images images3
        WHERE (pools0.number = insrel.snumber
        AND images3.number = insrel.dnumber)
 */
        System.out.println("Query: " + sqlHandler.toSql(query3, sqlHandler));

        // Execute, result as clusternodes.
        List nodes3 = database.getNodes(query3, new ClusterBuilder(mmbase));
        Iterator iNodes3 = nodes3.iterator();
        while (iNodes3.hasNext()) {
            ClusterNode node = (ClusterNode) iNodes3.next();
            System.out.println("Clusternode: " + node);
        }

/*
 Returns: 
        clusternodes with these fields: pools0.number, insrel.number, images3.number      
 */ 
        
        // EXAMPLE 4: query retrieving resultnodes.
        BasicSearchQuery query4 = new BasicSearchQuery();
        BasicStep step4a = query4.addStep(pools);
        // Add some fields with field aliases.
        query4.addField(step4a, pools.getField("number")).setAlias("field1");
        query4.addField(step4a, pools.getField("name")).setAlias("field2");
        query4.addField(step4a, pools.getField("description")).setAlias("field3");
        
/*
 Query:
        SELECT 
            pools.number as field1,
            pools.name as field2,
            pools.description as field3
        FROM 
            <basename>_pools pools
 */
        System.out.println("Query: " + sqlHandler.toSql(query4, sqlHandler));

        // Execute, result as resultnodes.
        List nodes4 = database.getNodes(query4, new ResultBuilder(mmbase, query4));
        Iterator iNodes4 = nodes4.iterator();
        while (iNodes4.hasNext()) {
            ResultNode node = (ResultNode) iNodes4.next();
            System.out.println("Resultnode: " + node);
        }

/*
 Returns: 
        resultnodes with these fields: field1, field2, field3      
 */ 

        // EXAMPLE 5: query retrieving aggregated resultnodes.
        BasicSearchQuery query5 = new BasicSearchQuery(true);
        BasicStep step5a = query5.addStep(pools);
        // Add some aggregated fields with field aliases.
        query5.addAggregatedField(step5a, pools.getField("number"), 
            AggregatedField.AGGREGATION_TYPE_COUNT).setAlias("count");
        query5.addAggregatedField(step5a, pools.getField("number"), 
            AggregatedField.AGGREGATION_TYPE_MIN).setAlias("min_number");
        query5.addAggregatedField(step5a, pools.getField("number"), 
            AggregatedField.AGGREGATION_TYPE_MAX).setAlias("max_number");
        
/*
 Query:
        SELECT 
            COUNT(pools.number) as count,
            MIN(pools.number) as min_number,
            MAX(pools.number) as max_number
        FROM 
            <basename>_pools pools
 */
        System.out.println("Query: " + sqlHandler.toSql(query5, sqlHandler));

        // Execute, result as resultnodes.
        List nodes5 = database.getNodes(query5, new ResultBuilder(mmbase, query5));
        Iterator iNodes5 = nodes5.iterator();
        while (iNodes5.hasNext()) {
            ResultNode node = (ResultNode) iNodes5.next();
            System.out.println("Resultnode: " + node);
        }

/*
 Returns: 
        a resultnode with these fields: count, min_number, max_number      
 */ 

        System.exit(0);
    }
}
