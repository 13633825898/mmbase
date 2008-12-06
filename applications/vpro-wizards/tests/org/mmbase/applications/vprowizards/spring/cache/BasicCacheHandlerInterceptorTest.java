package org.mmbase.applications.vprowizards.spring.cache;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BasicCacheHandlerInterceptorTest extends TestCase {
    
    private ApplicationContext context;

    @Override
    protected void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("org/mmbase/applications/vprowizards/spring/cache/applicationContext.xml");
    }
    
    public void test_application_context(){
        assertNotNull(context.getBean("handlerInterceptor"));
        assertNotNull(context.getBean("dummyCacheWrapper"));
        BasicCacheHandlerInterceptor handlerInterceptor = (BasicCacheHandlerInterceptor) context.getBean("handlerInterceptor");
        assertNotNull(handlerInterceptor.getCacheNameResolverFactory());
        
        assertTrue(DummyCacheWrapper.class.isInstance(handlerInterceptor.getCacheWrapper()));
        assertEquals(TokenizerCacheNameResolver.class, handlerInterceptor.getCacheNameResolverFactory().getClazz());
    }
    public void test_some_more(){
    }

}
