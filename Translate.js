import React, { useState } from 'react';
import translate from 'google-translate-api';

const Translate = () => {
  const [textToTranslate, setTextToTranslate] = useState('');
  const [translatedText, setTranslatedText] = useState('');
  const [targetLanguage, setTargetLanguage] = useState('en'); // Default target language is English

  const handleTranslation = () => {
    translate(textToTranslate, { to: targetLanguage })
      .then((res) => {
        setTranslatedText(res.text);
      })
      .catch((err) => {
        console.error('Error translating:', err);
        setTranslatedText('Error translating the text.');
      });
  };

  return (
    <div>
      <h2>Translation App</h2>
      <textarea
        value={textToTranslate}
        onChange={(e) => setTextToTranslate(e.target.value)}
        placeholder="Enter text to translate"
      />
      <br />
      <label htmlFor="targetLanguage">Select Target Language:</label>
      <select
        id="targetLanguage"
        value={targetLanguage}
        onChange={(e) => setTargetLanguage(e.target.value)}
      >
        <option value="en">English</option>
        <option value="es">Spanish</option>
        {/* Add more language options as needed */}
      </select>
      <br />
      <button onClick={handleTranslation}>Translate</button>
      <br />
      <h3>Translated Text:</h3>
      <div>{translatedText}</div>
    </div>
  );
};

export default Translate;