using System;
using System.Collections.Generic;
using System.IO;
using ETEUtils;
using CreateRAString;
using Newtonsoft.Json;

namespace HRAUnsolicited
{
    public class HraInvitation
    {
        public string inv;
        public string code;
        public string remoteHost;
     
        private HraInvitation(UnsolicitedConfig conf)
        {
            remoteHost = conf.RemoteHost;
        }


        public static HraInvitation create(UnsolicitedConfig conf)
        {
            HraInvitation inst = new HraInvitation(conf);
            inst.start();

            return inst;
        }


        private void start()
        {
            this.inv = RATicketGenerator.RequestRATicket(remoteHost);
        }

        public string ToJson()
        {
            return JsonConvert.SerializeObject(this);
        }
    }
}
