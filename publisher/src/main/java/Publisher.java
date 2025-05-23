public class Publisher {
    public static void main(String[] args) {
        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args)) {
            com.zeroc.Ice.ObjectPrx obj = communicator.stringToProxy("IceStorm/TopicManager:tcp -h localhost -p 10000");
            com.zeroc.IceStorm.TopicManagerPrx topicManager = com.zeroc.IceStorm.TopicManagerPrx.checkedCast(obj);
            com.zeroc.IceStorm.TopicPrx topic = null;
            while(topic == null)
            {
                try
                {
                    topic = topicManager.retrieve("Weather");
                }
                catch(com.zeroc.IceStorm.NoSuchTopic ex)
                {
                    try
                    {
                        topic = topicManager.create("Weather");
                    }
                    catch(com.zeroc.IceStorm.TopicExists ex2)
                    {
                        // Another client created the topic.
                    }
                }
            }
        
            com.zeroc.Ice.ObjectPrx pub = topic.getPublisher().ice_oneway();
            Demo.MonitorPrx monitor = Demo.MonitorPrx.uncheckedCast(pub);
            while(true)
                {
                    Demo.Measurement m = getMeasurement();
                    monitor.report(m);
                    Thread.sleep(10000);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Demo.Measurement getMeasurement() {
        Demo.Measurement m = new Demo.Measurement();
        m.tower = "Torre A";
        m.windSpeed = 10 + (float)(Math.random() * 10);
        m.windDirection = 180;
        m.temperature = 25 + (float)(Math.random() * 5);
        return m;
    }
}
