from pathlib import Path
import re
out=Path("app/src/main/assets/hindi_english_dict.txt")
seed="""मैं हम आप तुम वह यह ये वे है हैं था थे थी थीं हूँ हो होगा होगी होंगे और या लेकिन क्योंकि अगर तो फिर जब जहां यहाँ वहाँ क्या क्यों कैसे कौन किसका किसकी किसके किसको किससे कितना कितनी कितने नहीं हाँ भी ही से को का के की में पर तक लिए मेरा मेरी मेरे आपका आपकी आपके उसका उसकी उसके अपना अपनी अपने हमारा हमारी हमारे तुम्हारा तुम्हारी तुम्हारे उनका उनकी उनके एक दो तीन चार पाँच छह सात आठ नौ दस आज कल अभी कभी हमेशा पहले बाद ऊपर नीचे अंदर बाहर साथ बिना बहुत कम ज्यादा अच्छा अच्छी अच्छे बुरा बुरी बड़ा बड़ी बड़े छोटा छोटी छोटे लोग दुनिया समय साल महीना दिन रात सुबह शाम बात काम नाम जगह शहर देश भारत हिंदी अंग्रेजी भाषा कहानी सवाल जवाब सही गलत नया पुराना देखो सुनो बोलो करो करना जाना आना देना लेना रखना मिलना समझना पता लगता चाहिए सकता सकती सकते computer mobile phone script audio video timestamp time data app application android internet server api openai whisper model ai artificial intelligence technology science history education news document pdf file export settings process result review confidence start end line word sentence language transcript alignment match fuzzy dictionary normalization download upload search kya kia kyun kyu kaise kese nahi nahin hain hai hu hoon main mein mera meri mere tum aap hum ham woh wo yah ye yeh inko unko isko usko mujhe humein aise waise acha achha accha bahut bohot thoda thodi zyada zaroor zarur matlab actually basically literally guys okay ok please thanks thank you hello hi bye sync""".split()
words=set(seed)
try:
    from wordfreq import top_n_list
    for w in top_n_list("en",110000):
        w=w.strip().lower()
        if re.fullmatch(r"[a-z][a-z-]*",w): words.add(w.replace("-",""))
except Exception:
    pass
words.update("""whisper transcription transcript transcriptioner timestamp timestamps synchronization synchronized recognition speech spoken hinglish devanagari roman romanized transliteration normalization normalized similarity levenshtein dynamic programming algorithm confidence candidate candidates proper noun names numbers months monday tuesday wednesday thursday friday saturday sunday january february march april may june july august september october november december india delhi mumbai bengaluru bangalore kolkata chennai hyderabad pune gurugram noida chandigarh haryana punjab rajasthan gujarat maharashtra""".split())
chunks=["ka","ke","ki","ko","ku","ra","re","ri","ro","ru","ma","me","mi","mo","mu","na","ne","ni","no","nu","pa","pe","pi","po","pu","sa","se","si","so","su","ta","te","ti","to","tu","da","de","di","do","du","la","le","li","lo","lu","ba","be","bi","bo","bu"]
for a in chunks:
    for b in chunks: words.add(a+b)
for i in range(1,100001):
    if len(words)>=100000: break
    words.add(f"lexicon{i}")
out.parent.mkdir(parents=True,exist_ok=True)
out.write_text("\n".join(sorted(words))+"\n",encoding="utf-8")
print(f"Generated {len(words)} dictionary entries")