package com.cloudogu.scm.ssh.sample;


import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.command.Command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public abstract class CharBasedCommand implements Command {

  protected BufferedReader reader;
  protected BufferedWriter writer;
  protected BufferedWriter errWriter;
  protected ExitCallback callback;

  @Override
  public void setInputStream(InputStream in) {
    this.reader = createReader(in);
  }

  @Override
  public void setOutputStream(OutputStream out) {
    this.writer = createWriter(out);
  }

  @Override
  public void setErrorStream(OutputStream err) {
    this.errWriter = createWriter(err);
  }

  @Override
  public void setExitCallback(ExitCallback callback) {
    this.callback = callback;
  }

  @Override
  public void destroy() throws Exception {
    writer.flush();
    errWriter.flush();
  }


  private BufferedReader createReader(InputStream in) {
    return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
  }

  private BufferedWriter createWriter(OutputStream out) {
    return new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
  }
}
