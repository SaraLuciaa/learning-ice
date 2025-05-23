class MonitorI implements Demo.Monitor
{
    @Override
    public void report(Demo.Measurement m, com.zeroc.Ice.Current curr)
    {
        System.out.println(
            "Measurement report:\n" +
            "  Tower: " + m.tower + "\n" +
            "  W Spd: " + m.windSpeed + "\n" +
            "  W Dir: " + m.windDirection + "\n" +
            "   Temp: " + m.temperature + "\n");
    }
}