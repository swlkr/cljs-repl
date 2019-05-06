'use babel';

export default class CljsReplView {

  constructor(serializedState) {
    // Create root element
    this.element = document.createElement('div');
    this.element.classList.add('cljs-repl');

    // Create mini editor
    //this.miniEditor = new TextEditor({ mini: true });
    //this.miniEditor.element.addEventListener('blur', this.close.bind(this));
    //this.miniEditor.setPlaceholderText('localhost:5555');

    // Create input element
    this.input = document.createElement('input');
    this.input.type = 'text';
    this.input.setAttribute('style', 'padding: 7px;');
    this.input.classList.add('input-text');
    this.input.classList.add('native-key-bindings');
    this.input.value = 'localhost:5555';
    this.input.autofocus = 'autofocus';
    this.element.appendChild(this.input);

    // Create message element
    // const message = document.createElement('div');
    // message.textContent = 'The CljsRepl package is Alive! It\'s ALIVE!';
    // message.classList.add('message');
    // this.element.appendChild(message);
  }

  // Returns an object that can be retrieved when package is activated
  serialize() {}

  // Tear down any state and detach
  destroy() {
    this.element.remove();
  }

  getElement() {
    return this.element;
  }

}
