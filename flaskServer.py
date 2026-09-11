from flask import Flask, jsonify

app = Flask(__name__)

@app.route('/prices')
def prices():
    data = [
        {"symbol": "BTC", "name": "Bitcoin", "price": 62000.0},
        {"symbol": "ETH", "name": "Ethereum", "price": 3100.0},
        {"symbol": "SOL", "name": "Solana", "price": 145.0},
        {"symbol": "ADA", "name": "Cardano", "price": 0.45},
        {"symbol": "XRP", "name": "Ripple", "price": 0.52},
        {"symbol": "DOT", "name": "Polkadot", "price": 7.10},
        {"symbol": "DOGE", "name": "Dogecoin", "price": 0.15},
        {"symbol": "AVAX", "name": "Avalanche", "price": 35.20},
        {"symbol": "LINK", "name": "Chainlink", "price": 14.30},
        {"symbol": "MATIC", "name": "Polygon", "price": 0.68},
        {"symbol": "LTC", "name": "Litecoin", "price": 82.50},
    ]
    return jsonify(data)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
    # debug true , per non restart ogni votla