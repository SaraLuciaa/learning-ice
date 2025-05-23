public class Suscriber {
    public static void main(String[] args) {
        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.sub")) {
            com.zeroc.Ice.ObjectPrx obj = communicator.stringToProxy("IceStorm/TopicManager:tcp -h localhost -p 10000");
            com.zeroc.IceStorm.TopicManagerPrx topicManager = com.zeroc.IceStorm.TopicManagerPrx.checkedCast(obj);
        
            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("MonitorAdapter");
        
            Demo.Monitor monitor = new MonitorI();
            com.zeroc.Ice.ObjectPrx proxy = adapter.addWithUUID(monitor).ice_oneway();
            adapter.activate();
        
            com.zeroc.IceStorm.TopicPrx topic = null;
            try
            {
                topic = topicManager.retrieve("Weather");
                java.util.Map<String, String> qos = null;
                topic.subscribeAndGetPublisher(qos, proxy);
            }
            catch(com.zeroc.IceStorm.NoSuchTopic ex)
            {
                // Error! No topic found!
            }
        
            communicator.waitForShutdown();
        
            topic.unsubscribe(proxy);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}