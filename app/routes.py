from flask import render_template, flash, redirect, jsonify, url_for
from app import app
from app.forms import LoginForm


class UserData:
    def __init__(self):
        self.wordlist = list()

    def add_word(self, word):
        word = word.lower()
        if word not in self.wordlist:
            self.wordlist.append(word)

    def remove_word(self, word):
        word = word.lower()
        if word in self.wordlist:
            self.wordlist.remove(word)


users = dict(test=UserData())
username = 'test'
users[username].wordlist = ["one", "two", "five", "41", "hello"]


@app.route('/')
@app.route('/index')
def index():
    test_text = "Hello my 41 hello, how are 41 you hello. How are you doing?"
    splitted_test_text = test_text.split()

    highlights = [0] * len(splitted_test_text)
    words_to_highlight = users[username].wordlist
    words_to_highlight = [x.lower() for x in words_to_highlight]

    for w in words_to_highlight:
        for i in range(len(splitted_test_text)):
            if w == splitted_test_text[i].lower():
                highlights[i] = 1

    return render_template('index.html', title='Home', splitted_text=zip(splitted_test_text, highlights))


@app.route('/login', methods=['GET', 'POST'])
def login():
    form = LoginForm()
    if form.validate_on_submit():
        flash('Login requested for user {}, remember_me={}'.format(
            form.username.data, form.remember_me.data))
        return redirect('/index')
    return render_template('login.html', title='Sign In', form=form)


# API Section.

@app.route('/api/add/<word>', methods=['GET', 'POST'])
def api_add(word):
    users[username].add_word(word)
    return word


@app.route('/api/remove/<word>', methods=['GET', 'POST'])
def api_remove(word):
    users[username].remove_word(word)
    return word


@app.route('/api/wordlist', methods=['GET', 'POST'])
def api_wordlist():
    wordlist = users[username].wordlist
    res = jsonify(wordlist=wordlist)
    return res


# Reading Section.

@app.route('/reading/add/<word>', methods=['GET', 'POST'])
def reading_add(word):
    users[username].add_word(word)
    return redirect(url_for("index"))


@app.route('/reading/remove/<word>', methods=['GET', 'POST'])
def reading_remove(word):
    users[username].remove_word(word)
    return redirect(url_for("index"))


# Web Section.

@app.route('/wordlist', methods=['GET', 'POST'])
def web_wordlist():
    wordlist = users[username].wordlist
    wordlist = sorted(wordlist)

    if len(wordlist) == 0:
        wordlist = None

    return render_template('wordlist.html', title='List of Words', wordlist=wordlist)


@app.route('/remove/<word>', methods=['GET', 'POST'])
def web_remove(word):
    api_remove(word)
    web_wordlist()
    return redirect(url_for("web_wordlist"))
