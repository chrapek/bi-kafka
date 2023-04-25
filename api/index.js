const express = require('express')
const pgp = require('pg-promise')({})
const cors = require('cors')

const app = express()
const port = 3000

const db = pgp('postgres://postgres:postgres@localhost:5432/bi-stream')

app.use(cors())

app.get('/subscribe-keys', async (req, res) => {
    const subkeys = await db.many('SELECT DISTINCT "subscribeKey" FROM pn_aggregate');
    res.json(subkeys.map(d => d.subscribeKey))
})

app.get('/channels', async (req, res) => {
    const subscribeKey = req.query.subscribeKey;
    if (!subscribeKey) {
        res.status(400).send('Subscribe key is required')
    }
    const channels = await db.any(`
        SELECT "subscribeKey", channel, SUM(count) as sum
        FROM pn_aggregate 
        WHERE "subscribeKey" = $1
        GROUP BY "subscribeKey", channel
        ORDER BY sum DESC
        LIMIT 10`,
        subscribeKey
    );
    res.json(channels)
})

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
})