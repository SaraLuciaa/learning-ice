module Demo
{
    struct Message {
        string id;
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