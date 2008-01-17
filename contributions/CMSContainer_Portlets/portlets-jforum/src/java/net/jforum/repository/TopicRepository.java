/*
 * Copyright (c) Rafael Steil
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * This file creation date: 05/04/2004 - 20:11:44
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.jforum.cache.CacheEngine;
import net.jforum.cache.Cacheable;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.TopicDAO;
import net.jforum.entities.Topic;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * Repository for the last n topics for each forum.
 * 
 * @author Rafael Steil
 * @author James Yong
 * @version $Id: TopicRepository.java,v 1.1 2008-01-17 08:04:50 mguo Exp $
 */
public class TopicRepository implements Cacheable
{
	private static int maxItems = SystemGlobals.getIntValue(ConfigKeys.TOPICS_PER_PAGE);
	
	private static final String FQN = "topics";
	private static final String RECENT = "recent";
	private static final String FQN_FORUM = FQN + "/byforum";
	private static final String RELATION = "relation";
	private static final String FQN_LOADED = FQN + "/loaded";
	
	private static CacheEngine cache;
	
	/**
	 * @see net.jforum.cache.Cacheable#setCacheEngine(net.jforum.cache.CacheEngine)
	 */
	public void setCacheEngine(CacheEngine engine)
	{
		cache = engine;
	}
	
	public static boolean isLoaded(int forumId)
	{
		return "1".equals(cache.get(FQN_LOADED, Integer.toString(forumId)));
	}

	/**
	 * Add topic to the FIFO stack
	 * 
	 * @param topic The topic to add to stack
	 */
	public synchronized static void pushTopic(Topic topic) throws Exception
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.TOPIC_CACHE_ENABLED)) {
			int limit = SystemGlobals.getIntValue(ConfigKeys.RECENT_TOPICS);
			
			LinkedList l = (LinkedList)cache.get(FQN, RECENT);
			if (l == null || l.size() == 0) {
				l = new LinkedList(loadMostRecentTopics());
			}
			
			l.remove(topic);
			l.addFirst(topic);
			
			while (l.size() > limit) {
				l.removeLast();
			}
			
			cache.add(FQN, RECENT, l);
		}
	}

	/**
	 * Remove topic to the FIFO stack
	 * 
	 * @param topic The topic to remove from stack
	 */
	public synchronized static void popTopic(Topic topic) throws Exception
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.TOPIC_CACHE_ENABLED)) {
			List l = (List)cache.get(FQN, RECENT);
			
			if (l == null || l.size() == 0) {
				l = new LinkedList(loadMostRecentTopics());
			}
			
			l.remove(topic);
			cache.add(FQN, RECENT, l);
		}
	}	

	/**
	 * Get all cached recent topics. 
	 * 
	 */	
	public static List getRecentTopics() throws Exception
	{
		List l = (List)cache.get(FQN, RECENT);
		
		if (l == null || l.size() == 0
				|| !SystemGlobals.getBoolValue(ConfigKeys.TOPIC_CACHE_ENABLED)) {
			l = loadMostRecentTopics();
		}
		
		return new ArrayList(l);
	}	

	/**
	 * Add recent topics to the cache
	 */
	public static List loadMostRecentTopics() throws Exception
	{
		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		int limit = SystemGlobals.getIntValue(ConfigKeys.RECENT_TOPICS);
		
		List l = tm.selectRecentTopics(limit);
		cache.add(FQN, RECENT, new LinkedList(l));
		
		return l;
	}
	/**
	 * Add topics to the cache
	 * 
	 * @param forumId The forum id to which the topics are related
	 * @param topics The topics to add
	 */
	public static void addAll(int forumId, List topics)
	{
		synchronized (FQN_FORUM) {
			cache.add(FQN_FORUM, Integer.toString(forumId), new LinkedList(topics));
			
			Map m = (Map)cache.get(FQN, RELATION);
			
			if (m == null) {
				m = new HashMap();
			}
			
			Integer fId = new Integer(forumId);
			
			for (Iterator iter = topics.iterator(); iter.hasNext(); ) {
				Topic t = (Topic)iter.next();
				
				m.put(new Integer(t.getId()), fId);
			}
			
			cache.add(FQN, RELATION, m);
			cache.add(FQN_LOADED, Integer.toString(forumId), "1");
		}
	}
	
	/**
	 * Clears the cache
	 * 
	 * @param forumId The forum id to clear the cache
	 */
	public static void clearCache(int forumId) throws Exception
	{
		synchronized (FQN_FORUM) {
			cache.add(FQN_FORUM, Integer.toString(forumId), new LinkedList());
			cache.remove(FQN, RELATION);
		}
	}
	
	/**
	 * Adds a new topic to the cache
	 * 
	 * @param topic The topic to add
	 */
	public static void addTopic(Topic topic)
	{
		if (!SystemGlobals.getBoolValue(ConfigKeys.TOPIC_CACHE_ENABLED)) {
			return;
		}
		
		synchronized (FQN_FORUM) {
			String forumId = Integer.toString(topic.getForumId());
			LinkedList list = (LinkedList)cache.get(FQN_FORUM, forumId);
			
			if (list == null) {
				list = new LinkedList();
				list.add(topic);
			}
			else {
				boolean contains = list.contains(topic);
				
				if (!contains && list.size() + 1 > maxItems) {
					list.removeLast();
				}
				else if (contains) {
					list.remove(topic);
				}
				
				int index = 0;
				
				for (Iterator iter = list.iterator(); iter.hasNext(); index++) {
					Topic current = (Topic)iter.next();
					
					if (current.getType() == Topic.TYPE_ANNOUNCE) {
						if (topic.getType() == Topic.TYPE_ANNOUNCE) {
							list.add(index, topic);
							break;
						}

						continue;
					}
					
					if (current.getType() == Topic.TYPE_STICKY) {
						if (topic.getType() == Topic.TYPE_STICKY) {
							list.add(index, topic);
							break;
						}
						
						continue;
					}

					list.add(index, topic);
					break;
				}
			}
			
			cache.add(FQN_FORUM, forumId, list);
		
			Map m = (Map)cache.get(FQN, RELATION);
			
			if (m == null) {
				m = new HashMap();
			}
			
			m.put(new Integer(topic.getId()), new Integer(forumId));
			
			cache.add(FQN, RELATION, m);
		}
	}
	
	/**
	 * Updates a cached topic
	 * 
	 * @param topic The topic to update
	 */
	public static void updateTopic(Topic topic)
	{
		synchronized (FQN_FORUM) {
			String forumId = Integer.toString(topic.getForumId());
			List l = (List)cache.get(FQN_FORUM, forumId);
			
			if (l != null) {
				int index = l.indexOf(topic);
				
				if (index > -1) {
					l.set(index, topic);
					cache.add(FQN_FORUM, forumId, l);
				}
			}
		}
	}
		
	/**
	 * Removes a topic from the cache.
	 * @param topic The topic to remove. The object instance should
	 * have at least the topic_id and forum_id fields set
	 */
	public static void remove(Topic topic)
	{
		synchronized (FQN_FORUM) {
			if (topic.getForumId() == 0) {
				throw new IllegalArgumentException("Forum id cannot be empty");
			}
			
			String forumId = Integer.toString(topic.getForumId());
			List l = (List)cache.get(FQN_FORUM, forumId);
			
			if (l != null) {
				l.remove(topic);
				cache.add(FQN_FORUM, forumId, l);
				
				// Relation
				Map m = (Map)cache.get(FQN, RELATION);
				
				if (m != null) {
					m.remove(new Integer(topic.getId()));
					cache.add(FQN, RELATION, m);
				}
			}
		}
	}
	
	/**
	 * Gets a cached topic.
	 * 
	 * @param t The topic to try to get from the cache. The instance
	 * passed as argument should have ae least the topicId and forumId set
	 * @return The topic instance, if found, or <code>null</code> otherwise. 
	 */
	public static Topic getTopic(Topic t)
	{
		if (t.getForumId() == 0) {
			Map m = (Map)cache.get(FQN, RELATION);
			
			if (m != null) {
				Integer forumId = (Integer)m.get(new Integer(t.getId()));
				
				if (forumId != null) {
					t.setForumId(forumId.intValue());
				}
			}
			
			if (t.getForumId() == 0) {
				return null;
			}
		}
		
		List l = (List)cache.get(FQN_FORUM, Integer.toString(t.getForumId()));
		
		int index = -1;
		
		if (l != null) {
			index = l.indexOf(t);
		}
		
		return (index == -1 ? null : (Topic)l.get(index));
	}
	
	/**
	 * Checks if a topic is cached
	 * 
	 * @param topic The topic to verify
	 * @return <code>true</code> if the topic is cached, or <code>false</code> if not.
	 */
	public static boolean isTopicCached(Topic topic)
	{
		return ((List)cache.get(FQN_FORUM, Integer.toString(topic.getForumId()))).contains(topic);
	}
	
	/**
	 * Get all cached topics related to a forum. 
	 * 
	 * @param forumid The forum id 
	 * @return <code>ArrayList</code> with the topics.
	 */
	public static List getTopics(int forumid)
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.TOPIC_CACHE_ENABLED)) {
			synchronized (FQN_FORUM) {
				List returnList = (List)cache.get(FQN_FORUM, Integer.toString(forumid));

				if (returnList == null) {
					return new ArrayList();
				}
				
				return new ArrayList(returnList);
			}
		}
		
		return new ArrayList();
	}
}
