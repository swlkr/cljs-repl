'use babel';

import CljsReplView from './cljs-repl-view';
import { CompositeDisposable } from 'atom';
import EditorUtils from './editor-utils';
import net from 'net';

export default {

  cljsReplView: null,
  modalPanel: null,
  subscriptions: null,

  activate(state) {
    this.cljsReplView = new CljsReplView(state.cljsReplViewState);
    this.modalPanel = atom.workspace.addModalPanel({
      item: this.cljsReplView.getElement(),
      visible: false
    });

    // Events subscribed to in atom's system can be easily cleaned up with a CompositeDisposable
    this.subscriptions = new CompositeDisposable();

    // Register commands
    this.subscriptions.add(atom.commands.add('atom-workspace', {
      'cljs-repl:connect': () => this.showConnect(),
      'cljs-repl:send': () => this.send(),
      'cljs-repl:hidePanel': () => this.hideConnect(),
      'cljs-repl:connect2': () => this.connect(),
      'cljs-repl:disconnect': () => this.disconnect()
    }));
  },

  deactivate() {
    this.modalPanel.destroy();
    this.subscriptions.dispose();
    this.cljsReplView.destroy();
  },

  serialize() {
    return {
      cljsReplViewState: this.cljsReplView.serialize()
    };
  },

  showConnect() {
    return this.modalPanel.show();
  },

  hideConnect() {
    return this.modalPanel.hide();
  },

  connect() {
    this.client = new net.Socket();

    var value = this.cljsReplView.input.value;
    var [url, port] = value.split(':');

    this.client.connect(port, url, function() {
      atom.notifications.addInfo('Connected to ' + value)
    	//this.client.write('');
    });

    this.client.on('data', function(data) {
    	console.log('Received: ' + data);
    	//this.client.destroy(); // kill client after server's response
    });

    this.client.on('close', function() {
      atom.notifications.addInfo('Connection closed')
    });

    this.client.on('error', function(error) {
      atom.notifications.addError(error.message)
    })

    return this.modalPanel.hide();
  },

  send() {
    console.log('cljs-repl:send');

    let editor
    if (editor = atom.workspace.getActiveTextEditor()) {
      let range = EditorUtils.getCursorInClojureTopBlockRange(editor)
      let s = editor.getTextInBufferRange(range);
      this.client.write(s);
    }
  },

  disconnect() {
    this.client.destroy();
  }

};
