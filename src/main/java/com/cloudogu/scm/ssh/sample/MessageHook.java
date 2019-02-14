package com.cloudogu.scm.ssh.sample;

import com.github.legman.Subscribe;
import sonia.scm.EagerSingleton;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.PostReceiveRepositoryHookEvent;
import sonia.scm.repository.api.HookMessageProvider;

@Extension
@EagerSingleton
public class MessageHook {

  @Subscribe(async = false)
  public void handle(PostReceiveRepositoryHookEvent event) {
    HookMessageProvider messageProvider = event.getContext().getMessageProvider();
    messageProvider.sendMessage("***************************");
    messageProvider.sendMessage("BAM! ssh with hook support!");
    messageProvider.sendMessage("***************************");
  }
}
