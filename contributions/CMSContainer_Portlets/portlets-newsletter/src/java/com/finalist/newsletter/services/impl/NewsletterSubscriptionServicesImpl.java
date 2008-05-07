package com.finalist.newsletter.services.impl;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Node;

import com.finalist.newsletter.cao.NewsletterCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Term;
import static com.finalist.newsletter.domain.Subscription.STATUS;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;
import com.finalist.newsletter.services.NewsletterService;
import com.finalist.newsletter.util.DateUtil;
import com.finalist.newsletter.util.NewsletterSubscriptionUtil;
import com.finalist.cmsc.services.community.person.PersonService;
import com.finalist.cmsc.services.community.person.Person;

public class NewsletterSubscriptionServicesImpl implements NewsletterSubscriptionServices {

   private static Log log = LogFactory.getLog(NewsletterSubscriptionServicesImpl.class);

   NewsletterSubscriptionCAO subscriptionCAO;
   NewsletterCAO newsletterCAO;
   NewsletterService newsletterService;
   PersonService personService;

   public void setNewsletterCAO(NewsletterCAO newsletterCAO) {
      this.newsletterCAO = newsletterCAO;
   }

   public void setNewsletterService(NewsletterService newsletterService) {
      this.newsletterService = newsletterService;
   }

   public void setPersonService(PersonService personService) {
      this.personService = personService;
   }

   public void setSubscriptionCAO(NewsletterSubscriptionCAO subscriptionCAO) {
      this.subscriptionCAO = subscriptionCAO;
   }

   public List<Subscription> getSubscriptionList(String[] allowedLetters, int userId) {

      List<Subscription> subscriptionList = new ArrayList<Subscription>();
      for (String allowedLetter : allowedLetters) {
         subscriptionList.add(getSubscription(allowedLetter, Integer.toString(userId)));
      }
      return subscriptionList;
   }

   private Subscription getSubscription(String newsletterId, String userId) {
      int nId = Integer.parseInt(newsletterId);
      int uId = Integer.parseInt(userId);

      Subscription subscription = subscriptionCAO.getSubscription(nId, uId);
      Newsletter newsletter = newsletterCAO.getNewsletterById(nId);

      if (subscription == null) {
         subscription = new Subscription();
         subscription.setTerms(newsletter.getTerms());

      }
      else {
         Set<Term> newsletterTerms = newsletter.getTerms();
         Set<Term> subscriptionTerms = subscription.getTerms();
         for (Term term : newsletterTerms) {
            if (subscriptionTerms.size() == 0) {
               subscriptionTerms.addAll(newsletterTerms);
            }
            else if (!subscriptionTerms.contains(term)) {
               subscriptionTerms.add(term);
            }
         }
      }
      subscription.setNewsletter(newsletter);
      return subscription;
   }

   public List<Subscription> getNewSubscription(String[] allowedLetters) {
      List<Subscription> list = new ArrayList<Subscription>();
      int nodenumber;
      for (int i = 0; i < allowedLetters.length; i++) {
         nodenumber = Integer.parseInt(allowedLetters[i]);
         Newsletter newsletter = newsletterCAO.getNewsletterById(nodenumber);
         Subscription subscription = new Subscription();
         subscription.setNewsletter(newsletter);
         Newsletter test = subscription.getNewsletter();
         subscription.setTerms(newsletter.getTerms());
         list.add(subscription);
      }
      return list;
   }

   public void changeStatus(int userId, int newsletterId) {
      log.debug(String.format("user % change subscribe status on %s", userId, newsletterId));

      Subscription subscription = subscriptionCAO.getSubscription(newsletterId, userId);

      if (STATUS.ACTIVE.equals(subscription.getStatus())) {
         subscription.setStatus(STATUS.INACTIVE);
      }
      else {
         subscription.setStatus(STATUS.ACTIVE);
      }
      subscriptionCAO.modifySubscriptionStauts(subscription);
   }

   public void pause(String subscriptionId, String duration, String durationunit) {
      Subscription subscription = subscriptionCAO.getSubscriptionById(Integer.parseInt(subscriptionId));

      subscription.setStatus(STATUS.PAUSED);
      if (null != duration) {
         Date date = DateUtil.calculateDateByDuration(DateUtil.getCurrent(), Integer.parseInt(duration), durationunit);
         subscription.setPausedTill(date);
      }

      subscriptionCAO.updateSubscription(subscription);
   }


   public void pause(String subscriptionId, String resumeDate) {

      log.debug(String.format("Pasue subscription %s till %s", subscriptionId, resumeDate));

      Subscription subscription = subscriptionCAO.getSubscriptionById(Integer.parseInt(subscriptionId));

      subscription.setStatus(STATUS.PAUSED);
      if (null != resumeDate) {
         Date date = DateUtil.parser(resumeDate);
         subscription.setPausedTill(date);
      }

      subscriptionCAO.updateSubscription(subscription);
   }


   public boolean hasSubscription(int userId) {

      List<Node> list = subscriptionCAO.querySubcriptionByUser(userId);
      if (0 == list.size()) {
         return false;
      }
      else {
         return true;
      }
   }

   public void selectTermInLetter(int userId, int newsletterId, int termId) {
      log.debug("Add term " + termId + " to user" + userId + "'s newsletter:" + newsletterId);
      Subscription subscription = subscriptionCAO.getSubscription(newsletterId, userId);
      Set<Term> termList = subscription.getTerms();
      Iterator it = termList.iterator();
      for (int i = 0; i < termList.size(); i++) {
         Term term = (Term) it.next();
         if (termId == term.getId()) {
            term.setSubscription(true);
         }
      }
      subscriptionCAO.addSubscriptionTerm(subscription, termId);

   }

   public void unSelectTermInLetter(int userId, int newsletterId, int termId) {
      log.debug("Remove term " + termId + " to user " + userId + "'s newsletter:" + newsletterId);
      Subscription subscription = subscriptionCAO.getSubscription(newsletterId, userId);
      Set<Term> termList = subscription.getTerms();
      Iterator it = termList.iterator();
      for (int i = 0; i < termList.size(); i++) {
         Term term = (Term) it.next();
         if (termId == term.getId()) {
            term.setSubscription(false);
         }
      }
      subscriptionCAO.removeSubscriptionTerm(subscription, termId);
   }

   public void modifyStauts(int userId, int newsletterId, String status) {
      log.debug("user " + userId + " change subscription status of newsletter " + newsletterId + " to " + status);
      Subscription subscription = subscriptionCAO.getSubscription(newsletterId, userId);

      if (null == subscription) {
         subscriptionCAO.createSubscription(userId, newsletterId);
      }
      else {
         subscription.setStatus(STATUS.valueOf(status));
         subscriptionCAO.modifySubscriptionStauts(subscription);
      }
   }

   public void modifyFormat(int userId, int newsletterId, String format) {
      log.debug("User " + userId + " modify format of newsletter " + newsletterId + "to " + format);
      Subscription subscription = subscriptionCAO.getSubscription(newsletterId, userId);
      subscription.setMimeType(format);
      subscriptionCAO.modifySubscriptionFormat(subscription);
   }

   public boolean noSubscriptionRecord(int userId, int newsletterId) {
      Subscription subscription = subscriptionCAO.getSubscription(newsletterId, userId);
      return subscription == null;
   }

   public void addNewRecord(int userId, int newsletterId) {
      Subscription subscription = new Subscription();
      Newsletter newsletter = new Newsletter();
      newsletter.setId(newsletterId);
      subscription.setNewsletter(newsletter);
      subscription.setMimeType("text/html");
      subscription.setStatus(STATUS.ACTIVE);
      subscriptionCAO.addSubscriptionRecord(subscription, userId);
   }

   public void resume(String subscriptionId) {
      Subscription subscription = subscriptionCAO.getSubscriptionById(Integer.parseInt(subscriptionId));
      subscription.setStatus(STATUS.ACTIVE);
      subscriptionCAO.updateSubscription(subscription);
   }

   public void terminateUserSubscription(String subscriptionId) {
      Subscription subscription = subscriptionCAO.getSubscriptionById(Integer.parseInt(subscriptionId));
      subscription.setStatus(STATUS.INACTIVE);
      subscriptionCAO.updateSubscription(subscription);
   }

   public Subscription getSubscription(String sId) {
      int id = Integer.parseInt(sId);
      return subscriptionCAO.getSubscriptionById(id);
   }

   public List<Subscription> getActiveSubscription(int userId) {
      return subscriptionCAO.getSubscriptionByUserIdAndStatus(userId, STATUS.ACTIVE);
   }

   public int countAllSubscriptions() {
      return getAllSubscription().size();
   }

   public int countSubscriptionByNewsletter(int id) {
      return subscriptionCAO.getSubscription(id).size();
   }

   public List<Subscription> getAllSubscription() {
      List<Node> subscriptionNodes = subscriptionCAO.getAllSubscriptions();

      List<Subscription> subscriptions = new ArrayList<Subscription>();

      for (Node node : subscriptionNodes) {
         Subscription subscription = NewsletterSubscriptionUtil.convertFromNode(node);
         subscription.setNewsletter(newsletterService.getNewsletterBySubscription(subscription.getId()));
         subscriptions.add(subscription);
      }

      return subscriptions;
   }



   public List<Subscription> getSubscriptionsByNewsletterId(String i) {
      log.debug("Get all subscriptions of newsletter " + i);
      return subscriptionCAO.getSubscription(Integer.parseInt(i));
   }

   public Set<Subscription> getSubscriptions(String newsletterId, String name, String email) {
      Set<String> personIds = getPersonIdSet(name, email);

      Set<Subscription> result = new HashSet<Subscription>();

      System.out.printf("---------------"+personIds.size());

      for (Subscription subscription : getSubscriptionsByNewsletterId(newsletterId)) {
         if(personIds.contains(subscription.getSubscriberId())){
            result.add(subscription);
         }
      }

      return result;
   }

   private Set<String> getPersonIdSet(String name, String email) {
      Set<String> personIds = new HashSet<String>();

      Person person = new Person();
      if (StringUtils.isNotBlank(name)) {
         person.setLastName(name);
      }

      if (StringUtils.isNotBlank(email)) {
         person.setEmail(email);
      }

      List<Person> persons = personService.getLikePersons(person);
      for (Person p : persons) {
         personIds.add(p.getId().toString());
      }

      return personIds;
   }

   public List<Person> getAllSubscribers() {

      List<Person> persons = personService.getAllPersons();
      removeNoneSubscribers(persons);
      return persons;
   }

   private void removeNoneSubscribers(List<Person> persons) {
      for (int i = 0; i < persons.size(); i++) {
         Person person = persons.get(i);
         List subscrioptions = subscriptionCAO.getSubscriptionByUserIdAndStatus(person.getId().intValue(), null);
         if (subscrioptions.size() < 1) {
            persons.remove(i);
         }
      }
   }

   public List<Person> getAllSubscribers(String name, String email) {

      Person person = new Person();
      if (StringUtils.isNotBlank(name)) {
         person.setLastName(name);
      }

      if (StringUtils.isNotBlank(email)) {
         person.setEmail(email);
      }

      List<Person> persons = personService.getLikePersons(person);
      removeNoneSubscribers(persons);

      return persons;
   }

   public List<Subscription> getSubscriptionBySubscriber(String newsletterid) {
      return subscriptionCAO.getSubscriptionByUserIdAndStatus(Integer.parseInt(newsletterid),null);
   }

   public Subscription getSubscription(int userId, int newsletterId) {
      return subscriptionCAO.getSubscription(newsletterId,userId);
   }
}
