module Demo
{
    struct Message {
        string messageId;
        string text;
    };

    interface AckService {
        void confirm(string messageId);
    }

    interface Printer
    {
        void printString(Message m, AckService* ack);
    }
}