from flask import render_template, flash, redirect
from app import app
from app.forms import LoginForm
from flask import jsonify


class UserData:
    def __init__(self):
        self.word_list = list()

    def add_word(self, word):
        word = word.lower()
        if word not in self.word_list:
            self.word_list.append(word)

    def remove_word(self, word):
        word = word.lower()
        if word in self.word_list:
            self.word_list.remove(word)


@app.route('/')
@app.route('/index')
def index():
    user = {'username': 'Miguel'}
    posts = [
        {
            'author': {'username': 'John'},
            'body': 'Beautiful day in Portland!'
        },
        {
            'author': {'username': 'Susan'},
            'body': 'The Avengers movie was so cool!'
        }
    ]
    return render_template('index.html', title='Home', user=user, posts=posts)


@app.route('/login', methods=['GET', 'POST'])
def login():
    form = LoginForm()
    if form.validate_on_submit():
        flash('Login requested for user {}, remember_me={}'.format(
            form.username.data, form.remember_me.data))
        return redirect('/index')
    return render_template('login.html', title='Sign In', form=form)


@app.route('/add/<word>', methods=['GET', 'POST'])
def add(word):
    res = jsonify(word=word)
    return res
