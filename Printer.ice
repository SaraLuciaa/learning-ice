module Demo
{
    struct Message {
        string messageId;
        string text;
    };

    interface Printer
    {
        void printString(Message m);
    }

    interface AckService {
        void confirm(string messageId);
        bool isAcked(string messageId);
    }
}