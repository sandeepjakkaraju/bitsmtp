import pickle
import pandas as pd
from flask import Flask, request, jsonify
import nltk
nltk.download('stopwords')
from nltk.corpus import stopwords
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score

# Load the pickled model
with open('finalized_model.sav', 'rb') as f:
    model = pickle.load(f)

# Create a Flask app
app = Flask(__name__)

# Define a predict endpoint
@app.route('/predict', methods=['POST'])
def predict():
    
    idf = pd.read_csv('AllMailsOut.csv')  
    spam = pd.read_csv('enron_spam_data.csv') 
    spamn = spam[spam['Spam/Ham']=='spam']
    spamn.rename({'Subject': 'SUBJECT', 'Message': 'BODY'}, axis=1, inplace=True)
    

    final1 = idf[['SUBJECT','BODY']]
    final2 = spamn[['SUBJECT','BODY']]

    fintot = pd.concat([final1, final2], ignore_index=True)
    fintott = fintot
    fintott['TEXT'] = fintot['SUBJECT']+fintot['BODY']
    # replace NAN with empty string
    fintott.fillna('', inplace=True)

	### convert everything to lower
    fintott['TEXT'] = fintott['TEXT'].apply(lambda x: x.lower())


	### remove unwanted characters
    fintott['TEXT'] = fintott['TEXT'].str.replace(r'[^a-zA-Z]', ' ')

	### remove stop words
    stop_words = stopwords.words('english')
    fintott['TEXT'] = fintott['TEXT'].apply(lambda x: ' '.join([word for word in x.split() if word not in (stop_words)]))
    vectorizer = TfidfVectorizer()
    vectorizer.fit(fintott["TEXT"])

    # Get the email text from the request
    email_text = request.data.decode('utf-8')
    
    print(email_text)

    df = pd.DataFrame([email_text],index=[0],columns=['TEXT'])
	
	    
	# replace NAN with empty string
    df.fillna('', inplace=True)    

    ### convert everything to lower
    df['TEXT'] = df['TEXT'].apply(lambda x: x.lower())
    ### remove unwanted characters
    df['TEXT'] = df['TEXT'].replace(r'[^a-zA-Z]', ' ')


    ### remove stop words
    stop_words = stopwords.words('english')
    df['TEXT'] = df['TEXT'].apply(lambda x: ' '.join([word for word in x.split() if word not in (stop_words)]))

    df['TEXT'] = df['TEXT'].apply(lambda x: ' '.join([word for word in x.split() if word not in (stop_words)]))


    print(df)


    features = vectorizer.transform(df["TEXT"])

    X = features.toarray()


    # Use the model to predict if the email is spam or not
    prediction = model.predict(X)[0]
    
    # Return the prediction as a JSON response
    #return jsonify({'prediction': prediction})
    return str(prediction)
# Run the app
if __name__ == '__main__':
    app.run(debug=True)
