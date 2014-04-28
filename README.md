[![Build Status](https://travis-ci.org/martinraison/ScalaWit.png)](https://travis-ci.org/martinraison/ScalaWit)
ScalaWit
========

Scala client for Wit.AI

What this client can do for you:

* Handle HTTP asynchronous requests to Wit.AI
* Parse the JSON response and give you nice Scala objects to play with

## Examples

Documentation is on its way, in the meantime the API calls in [Wit.scala](https://github.com/martinraison/ScalaWit/blob/master/core/src/main/scala/com/scalawit/Wit.scala) should give you a pretty good idea of how to use this client.

###Creating the client (+ imports):
```bash
scala> import com.scalawit.Wit._, scala.concurrent._, scala.concurrent.duration._, javax.sound.sampled._, java.io._
scala> val client = new Wit("YOUR_TOKEN")
```

###Sending text input
```bash
scala> val message = Await.result(client.getMessage("hello"), 5 seconds)
scala> message.right.get.pretty
Message:
    msgId = dd206aea-eff9-441b-bcc4-19e42186912f
    msgBody = hello
    outcome = Outcome:
        intent = greetings
        entities = 
        confidence = 0.52
```

###Sending audio input
```bash
scala> val audio = AudioSystem.getAudioInputStream(new File("resources/hello.wav"))
scala> val message = Await.result(client.postSpeech(audio), 5 seconds)
scala> message.right.get.pretty
Message:
    msgId = 68c68ce4-225a-43ad-978b-b7a8086e18fd
    msgBody = hello
    outcome = Outcome:
        intent = greetings
        entities = 
        confidence = 0.525
        
```

###Other calls:

```bash
scala> val intents = Await.result(client.getIntents, 5 seconds)
scala> intents.right.get.pretty
* Intent:
    name = grab_beer
    doc = 
    id = 234f2dea-e7c2-4fc5-9636-a7f09c56de8f
    metadata = {"key": 34}
* Intent:
    name = greetings
    doc = 
    id = 1bc55974-d242-46e5-a605-de934d3eb0f1
```

The [Wit.AI API](https://wit.ai/docs/api) will likely be updated faster than this client.
Suggestions and pull requests welcome!

You're also welcome to submit an issue/pull request if you see JSON responses not being parsed correctly (failure or missing fields). Note that API calls also provide "-Raw" versions, which bypass parsing and just return a JSON string.

## License

The MIT License (MIT)

Copyright (c) 2014 Martin Raison

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
24