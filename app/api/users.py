from app.api import bp
from app.models import User
from flask import jsonify
from flask import g, abort
from app.api.auth import token_auth


@bp.route('/testapi', methods=['GET'])
@token_auth.login_required
def testapi():
    return 'works'


@bp.route('/users/<int:id>', methods=['GET'])
def get_user(id):
    return jsonify(User.query.get_or_404(id).to_dict())


# @bp.route('/users/<int:id>/followed', methods=['GET'])
# def get_followed(id):
#     pass


@bp.route('/users', methods=['POST'])
def create_user():
    pass
