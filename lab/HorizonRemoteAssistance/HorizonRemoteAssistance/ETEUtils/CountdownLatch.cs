using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;

namespace ETEUtils
{
    /*
     *  Replacement of .NET 4.0 System.Threading.CountdownEvent for .NET 2.0
     */
    public class CountdownLatch
    {
        private int remain;
        private EventWaitHandle signal;

        public CountdownLatch(int count)
        {
            remain = count;
            signal = new ManualResetEvent(false);
        }

        public void Signal()
        {
            // The last thread to signal also sets the event.
            if (Interlocked.Decrement(ref remain) == 0)
                signal.Set();
        }

        public void Wait()
        {
            signal.WaitOne();
        }
    }
}
