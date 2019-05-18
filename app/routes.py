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
users[username].wordlist = ["one", "two", "superfluidity", "41"]


@app.route('/')
@app.route('/index')
def index():
    test_text = "Superfluidity is the characteristic property of a fluid with zero viscosity which therefore flows without loss of kinetic energy. When stirred, a superfluid forms cellular vortices that continue to rotate indefinitely. Superfluidity occurs in two isotopes of helium (helium-3 and helium-4) when they are liquefied by cooling to cryogenic temperatures. It is also a property of various other exotic states of matter theorized to exist in astrophysics, high-energy physics, and theories of quantum gravity.[1] The phenomenon is related to Bose–Einstein condensation, but neither is a specific type of the other: not all Bose-Einstein condensates can be regarded as superfluids, and not all superfluids are Bose–Einstein condensates.[2] The theory of superfluidity was developed by Lev Landau."
    splitted_test_text = test_text.split()

    highlights = [0] * len(splitted_test_text)
    words_to_highlight = users[username].wordlist
    words_to_highlight = [x.lower() for x in words_to_highlight]

    for w in words_to_highlight:
        for i in range(len(splitted_test_text)):
            if w == splitted_test_text[i].lower():
                highlights[i] = 1

    wordlist = prepare_wordlist()

    return render_template('index.html', title='Home', splitted_text=zip(splitted_test_text, highlights), wordlist=wordlist)


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
    wordlist = prepare_wordlist()

    return render_template('wordlist_page.html', title='List of Words', wordlist=wordlist)


@app.route('/remove/<word>', methods=['GET', 'POST'])
def web_remove(word):
    api_remove(word)
    web_wordlist()
    return redirect(url_for("web_wordlist"))


# Utils.

def prepare_wordlist():
    wordlist = users[username].wordlist
    wordlist = [x.capitalize() for x in wordlist]
    wordlist = sorted(wordlist)

    if len(wordlist) == 0:
        wordlist = None

    return wordlist
