package com.vmware.vdi.broker.toolboxfilter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class MultipleReadRequestWrapper extends HttpServletRequestWrapper{
	private static Logger log = Logger.getLogger(MultipleReadRequestWrapper.class);
	 private ByteArrayOutputStream cachedBytes;
	public MultipleReadRequestWrapper(HttpServletRequest request) {
		super(request);

	}

	  @Override
	  public ServletInputStream getInputStream() throws IOException {
	    if (cachedBytes == null)
	      cacheInputStream();

	      return new CachedServletInputStream();
	  }

	  @Override
	  public BufferedReader getReader() throws IOException{
	    return new BufferedReader(new InputStreamReader(getInputStream()));
	  }

	  private void cacheInputStream() throws IOException {
	    /* Cache the inputstream in order to read it multiple times. For
	     * convenience, I use apache.commons IOUtils
	     */
	    cachedBytes = new ByteArrayOutputStream();
	    IOUtils.copy(super.getInputStream(), cachedBytes);
	  }

	  /* An inputstream which reads the cached request body */
	  public class CachedServletInputStream extends ServletInputStream {
	    private ByteArrayInputStream input;

	    public CachedServletInputStream() {
	      /* create a new input stream from the cached request body */
	      input = new ByteArrayInputStream(cachedBytes.toByteArray());
	    }

	    @Override
	    public int read() throws IOException {
	      return input.read();
	    }

		@Override
		public boolean isFinished() {
			return input.available() ==0;
		}

		@Override
		public boolean isReady() {

		    return input.available() > 0;
		}
//TODO: FIXME: error may happen if setReadListener is needed
		@Override
		public void setReadListener(ReadListener readListener) {
			log.error("ERROR: setReadListener is not supported!!!!!!");

		}
	  }
}
