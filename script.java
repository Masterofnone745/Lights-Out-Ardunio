// This script initializes the Highlight.js library to properly format the Arduino (C++) code block.
document.addEventListener('DOMContentLoaded', (event) => {
    // Calling highlightAll() scans the document for <pre><code>...</code></pre> blocks 
    // and applies syntax highlighting based on the specified language class (e.g., class="arduino").
    hljs.highlightAll();
});
