# Ktor Task Management Application

Kompletní průvodce technologiemi a architekturou použitými v projektu.

## Obsah
- [Architektura](#architektura)
- [Ktor Framework](#ktor-framework)
- [Ktor Plugins](#ktor-plugins)
- [GraphQL](#graphql)
- [REST API](#rest-api)
- [Dependency Injection](#dependency-injection)
- [Logování](#logování)
- [Databáze](#databáze)
- [Auth](#jwt-autentizace-s-ktor-auth-pluginem-kompletní-přehled)
- [Pre-signed URL a AWS S3](#aws-s3-a-pre-signed-url)

## Architektura

### Hexagonal Architecture (Ports & Adapters)

Hexagonální architektura, známá také jako Ports and Adapters, je návrhový vzor zaměřený na vytvoření nezávislé doménové logiky izolované od externích služeb a frameworků. Jádrem architektury je doménová vrstva obsahující business logiku a entity aplikace, která komunikuje s okolím pouze přes definovaná rozhraní (porty). Tyto porty se dělí na vstupní (jak okolí volá doménu, např. TaskService) a výstupní (jak doména volá okolí, např. TaskRepository). Implementace těchto portů jsou adaptéry - v našem projektu jsou vstupními adaptéry REST kontrolery a GraphQL resolvery, zatímco výstupními adaptéry jsou databázové implementace a AWS S3 klient. Tato architektura přináší několik výhod: nezávislost business logiky na frameworku, snadnou zaměnitelnost implementací (např. databáze), lepší testovatelnost (lze mockovat porty) a jasné hranice mezi vrstvami aplikace. V projektu jsou porty definovány v balíčku `domain.ports` a jejich implementace v `infrastructure` a `application`.

**Klíčové koncepty:**
- **Doménová vrstva** (core) - obsahuje business logiku a entity
- **Porty** - definují rozhraní pro interakci s doménou
  - **Vstupní porty** (primary/driving) - jak okolí volá doménu (např. `TaskService`)
  - **Výstupní porty** (secondary/driven) - jak doména volá okolí (např. `TaskRepository`)
- **Adaptéry** - implementace portů
  - **Vstupní adaptéry** - REST kontrolery, GraphQL resolvers
  - **Výstupní adaptéry** - databázové implementace, externí API klienti

**Výhody:**
- Nezávislost business logiky na frameworku
- Snadná zaměnitelnost implementací (např. databáze)
- Lepší testovatelnost (lze mockovat porty)
- Jasné hranice mezi vrstvami aplikace

## Ktor Framework

Ktor je lightweight asynchronní framework pro vytváření webových aplikací v Kotlinu. Vyvinutý společností JetBrains, využívá korutiny pro neblokující zpracování požadavků, což umožňuje efektivní obsluhu tisíců souběžných požadavků s minimálními systémovými nároky. Na rozdíl od tradičních Java frameworků jako Spring, Ktor je extrémně modulární - nabízí pouze základní funkcionalitu, kterou rozšiřujete pomocí pluginů podle potřeby. Framework používá type-safe DSL pro definici routingu a aplikační logiky, což eliminuje mnoho běžných runtime chyb. Další výhodou je multiplatformnost - Ktor aplikace lze spustit na JVM, Native nebo JS platformách. Za zmínku stojí i fakt, že Ktor není založen na reflexi ani anotacích, což přispívá k rychlejšímu startu a menšímu memory footprintu. Základními komponentami jsou Application (reprezentuje celou aplikaci), Engine (implementace serveru jako Netty, Tomcat nebo CIO), Pipelines (zpracování požadavků v definovaných fázích) a Plugins (rozšíření funkcionality).

**Klíčové vlastnosti:**
- Minimalistický a modulární (používá plugins)
- Plně asynchronní s využitím Kotlin korutin
- Type-safe DSL pro definici routingu a aplikační logiky
- Multiplatformní (JVM, Native, JS)
- Není založen na reflexi ani anotacích

**Základní komponenty:**
- **Application** - základní třída reprezentující Ktor aplikaci
- **Engine** - implementace serveru (Netty, Tomcat, CIO...)
- **Pipelines** - zpracování požadavků v fázích
- **Features/Plugins** - rozšíření funkcionality aplikace

**Příklad spuštění:**
```kotlin
fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello World!")
            }
        }
    }.start(wait = true)
}
```

## Ktor Plugins

### ContentNegotiation

ContentNegotiation plugin zajišťuje automatickou konverzi obsahu podle HTTP hlaviček `Content-Type` a `Accept`. Je klíčovým prvkem pro implementaci RESTful API, protože umožňuje aplikaci automaticky serializovat a deserializovat data do požadovaného formátu (JSON, XML, apod.) na základě požadavku klienta. V našem projektu je nakonfigurován s kotlinx.serialization pro práci s JSON. Plugin významně zjednodušuje kód, protože není nutné ručně převádět objekty - stačí použít `call.respond(objekt)` a Ktor se postará o vhodnou serializaci podle hlaviček. Podporuje různé serializační knihovny včetně Gson, Jackson a kotlinx.serialization, takže můžete zvolit tu, která nejlépe vyhovuje vašim potřebám. Navíc lze snadno přidat podporu pro vlastní formáty nebo upravit výchozí chování.

```kotlin
install(ContentNegotiation) {
    json(Json {
        prettyPrint = true
        isLenient = true
    })
}
```

**Funkce:** Automaticky serializuje/deserializuje objekty do formátů jako JSON, XML na základě požadavku klienta.

### Serialization - Kotlinx

Kotlinx.serialization je moderní knihovna pro serializaci a deserializaci objektů v Kotlinu, která nevyužívá reflexi, což ji činí výkonnou a multiplatformní. Pro použití stačí označit třídy anotací `@Serializable` a knihovna se postará o generování potřebného kódu v době kompilace. Podporuje nejen základní datové typy, ale i složité struktury včetně vnořených objektů, seznamů, map, nullovatelných typů a vlastních generických tříd. Výhodou oproti jiným serializačním knihovnám je plná kompatibilita s Kotlin specifickými konstrukcemi jako jsou data classes, sealed classes a nullable typy. V projektu používáme kotlinx.serialization pro serializaci modelů do JSON formátu, což zajišťuje konzistentní přenos dat mezi klientem a serverem. Knihovna také umožňuje pokročilou konfiguraci, jako je přizpůsobení názvů polí, ignorování určitých vlastností nebo definování alternativních serializačních strategií.

```kotlin
@Serializable
data class Task(val id: Int, val name: String)

// Použití:
val json = Json.encodeToString(task)
val task = Json.decodeFromString<Task>(json)
```

**Výhody:**
- Multiplatformní (JVM, JS, Native)
- Type-safe prostřednictvím Kotlin sealed classes
- Podpora vlastních serializátorů
- Kompatibilní s dalšími kotlinx knihovnami

### StatusPages

StatusPages plugin poskytuje centralizovaný mechanismus pro obsluhu chyb a výjimek v Ktor aplikaci. Místo implementace try-catch bloků v každé route umožňuje definovat globální handlery pro různé typy výjimek nebo HTTP status kódy. Tím se výrazně zlepšuje čitelnost kódu a konzistence chybových odpovědí napříč celou aplikací. V našem projektu používáme tento plugin pro mapování doménových výjimek (např. TaskNotFoundException) na příslušné HTTP odpovědi s jednotným formátem chybové zprávy. Plugin dále umožňuje přizpůsobit chování pro každý typ výjimky zvlášť - můžete generovat různý obsah, nastavit různé hlavičky nebo přesměrovat uživatele. Pro vývojáře je velmi užitečná možnost lokalizace chybových zpráv nebo přidání dodatečných informací pro debugging v development prostředí, zatímco v produkci jsou zobrazovány pouze základní informace.

```kotlin
install(StatusPages) {
    exception<TaskNotFoundException> { call, cause ->
        call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
    }
    status(HttpStatusCode.NotFound) { call, status ->
        call.respond(status, mapOf("error" to "Resource not found"))
    }
}
```

**Použití:** Místo implementace try-catch bloků v každé route umožňuje jednotnou obsluhu výjimek.

### Exposed

Exposed je lightweight ORM framework pro Kotlin vyvinutý společností JetBrains, který nabízí dva způsoby práce s databází: DSL (Domain Specific Language) s SQL-like syntaxí a DAO (Data Access Objects) s objektově-orientovaným přístupem. V našem projektu využíváme primárně DSL přístup, který poskytuje typově bezpečné SQL dotazy a plnou kontrolu nad generovanými SQL příkazy. Exposed automaticky řeší mapování mezi databázovými tabulkami a Kotlin objekty, přičemž podporuje transakce, cizí klíče, indexy a další pokročilé databázové funkce. Velkou výhodou je podpora mnoha databázových systémů (PostgreSQL, MySQL, H2, SQLite) bez nutnosti měnit aplikační kód. Framework také elegantně řeší běžné problémy jako je ochrana proti SQL injection nebo správná escape sekvence hodnot. V porovnání s jinými ORM nástroji je Exposed mnohem lehčí, rychlejší a více "Kotlin-friendly", což se odráží v čistším a stručnějším kódu při práci s databází.

**Dva způsoby práce:**
1. **DSL (Domain Specific Language)** - SQL-like způsob
2. **DAO (Data Access Objects)** - objektově-orientovaný způsob

```kotlin
// DSL příklad
object Tasks : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 128)
    override val primaryKey = PrimaryKey(id)
}

// Použití
val allTasks = transaction {
    Tasks.selectAll().map { 
        Task(it[Tasks.id], it[Tasks.name])
    }
}
```

**Výhody:**
- Typová bezpečnost SQL dotazů
- Plná kontrola nad generovanými SQL dotazy
- Podpora transakcí
- Podpora mnoha databázových systémů

### Routing

Routing plugin je základním stavebním kamenem Ktor aplikací, který poskytuje expressivní DSL pro definici HTTP endpointů a obsluhu příchozích požadavků. Umožňuje hierarchické uspořádání cest, což vede k přehlednějšímu a udržovatelnějšímu kódu, zejména u komplexních API. V našem projektu využíváme tuto hierarchii k logickému organizování endpointů podle doménových oblastí (např. všechny operace s úkoly jsou pod cestou "/tasks"). Routing podporuje typově bezpečné parametry a query string, což eliminuje mnoho běžných chyb při extrakci dat z URL. Další výhodou je možnost organizace routingu do samostatných souborů a funkcí, což zlepšuje modulárnost aplikace. Plugin také poskytuje flexibilní mechanismus pro implementaci middleware funkcí pomocí interceptorů, které mohou být aplikovány na celou aplikaci, skupinu cest nebo jednotlivé endpointy. To umožňuje snadno implementovat cross-cutting concerns jako autentizaci, autorizaci, validaci nebo metriky.

```kotlin
routing {
    route("/tasks") {
        get {
            call.respond(taskService.allTasks())
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            call.respond(taskService.getTask(id))
        }
    }
}
```

**Vlastnosti:**
- Hierarchické uspořádání cest
- Type-safe parametry a query string
- Možnost organizace routingu do samostatných souborů
- Podpora middleware pomocí interceptorů

### Static Content

Static Content plugin zjednodušuje servírování statických souborů jako jsou HTML, CSS, JavaScript, obrázky nebo jiné statické assety. Místo vytváření samostatného HTTP serveru pro tyto soubory můžete jednoduše nakonfigurovat Ktor, aby je automaticky obsluhoval z definovaných adresářů nebo ze zdrojů na classpath. V našem projektu využíváme tento plugin k servírování dokumentace API (SwaggerUI), CSS/JS souborů a dalších assetů potřebných pro webové rozhraní. Plugin nabízí pokročilé možnosti jako je nastavení cache hlaviček, MIME typů, defaultních souborů pro adresáře (např. index.html) nebo podmíněné servírování na základě HTTP hlaviček (ETag, If-Modified-Since). Výhodou centralizovaného řešení je také možnost snadného přidání zabezpečení, komprese nebo CDN integrace pro všechny statické soubory najednou.

```kotlin
install(StaticContent) {
    resources("static")
    files("fileDir")
}
```

**Účel:** Umožňuje jednoduše servírovat statické soubory (HTML, CSS, JS, obrázky) z definovaných adresářů.

### Postgresql

PostgreSQL je pokročilý open-source relační databázový systém s více než 30 lety vývoje, který se vyznačuje robustností, škálovatelností a bohatou funkcionalitou. V našem projektu slouží jako primární úložiště dat pro úkoly a související entity. PostgreSQL podporuje ACID transakce, cizí klíče, triggery a procedury, což zajišťuje integritu dat a umožňuje implementovat složitou business logiku přímo v databázi. Mezi jeho silné stránky patří nativní podpora JSON a JSONB datových typů, což je ideální pro ukládání semi-strukturovaných dat bez nutnosti ORM mapování. Databáze také nabízí pokročilé indexovací techniky (B-tree, Hash, GiST, GIN), fulltextové vyhledávání a širokou škálu datových typů včetně geometrických a síťových. V produkčním prostředí oceníte vysokou dostupnost, replikaci, partitioning tabulek a excelentní výkon i při velkých objemech dat.

**Klíčové vlastnosti:**
- ACID kompatibilita
- JSON podpora
- Rozšiřitelnost (extensions)
- Pokročilé datové typy (arrays, hstore, geometry...)
- Vysoký výkon

**Připojení v Ktor aplikaci:**
```kotlin
val config = HikariConfig().apply {
    jdbcUrl = "jdbc:postgresql://localhost:5432/mydatabase"
    username = "user"
    password = "password"
}
```

### CORS

CORS (Cross-Origin Resource Sharing) plugin řeší bezpečnostní omezení prohlížečů, které standardně blokují cross-origin HTTP požadavky z JavaScriptu. Toto omezení je kritické pro vývoj moderních webových aplikací, kde frontend běží na jiné doméně než backend API (např. React aplikace na `app.example.com` komunikující s API na `api.example.com`). Plugin umožňuje nakonfigurovat, které domény, HTTP metody a hlavičky jsou povoleny pro cross-origin požadavky. V našem projektu máme nastavenou liberální konfiguraci pro vývojové prostředí (`anyHost()`), ale pro produkci doporučujeme explicitně povolit pouze důvěryhodné domény. Plugin také automaticky obsluhuje preflight požadavky (OPTIONS), které prohlížeče posílají před vlastním požadavkem, a přidává potřebné CORS hlavičky do odpovědí. Správná konfigurace CORS je kritická pro bezpečnost aplikace - příliš restriktivní nastavení brání legitimnímu použití, zatímco příliš volné nastavení může vystavit API neoprávněnému přístupu.

```kotlin
install(CORS) {
    allowMethod(HttpMethod.Options)
    allowMethod(HttpMethod.Get)
    allowMethod(HttpMethod.Post)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Delete)
    allowHeader(HttpHeaders.ContentType)
    allowHeader(HttpHeaders.Authorization)
    anyHost()  // V produkci nahradit povolenými doménami
}
```

**Účel:** Umožňuje webovým aplikacím na jiných doménách komunikovat s vaším API.

## GraphQL

GraphQL je query language a runtime pro API, vyvinutý společností Facebook (nyní Meta), který umožňuje klientům specifikovat přesně jaká data potřebují, což eliminuje problémy s over-fetchingem a under-fetchingem běžné u tradičních REST API. V našem projektu implementujeme GraphQL pomocí knihovny `com.expediagroup:graphql-kotlin-server`, která poskytuje Kotlin-friendly způsob definice schématu pomocí anotací a tříd. Hlavními komponentami jsou Schema (definuje dostupné typy a operace), Query (dotazy pro čtení dat), Mutation (operace pro změnu dat) a Resolver (funkce pro získání konkrétních dat). GraphQL přináší několik významných výhod: klient má kontrolu nad tvarem a množstvím vrácených dat, je možné získat data z více zdrojů v jediném požadavku, schéma poskytuje silné typování a introspekci, a díky principu "jeden endpoint" se vývoj obejde bez verzování API. V projektu používáme hierarchické uspořádání Query a Mutation tříd, které zlepšuje organizaci kódu a rozšiřitelnost API s rostoucí komplexitou.

**Implementace v Ktor projektu:**
- Knihovna: `com.expediagroup:graphql-kotlin-server`

```kotlin
install(GraphQL) {
    schema {
        packages = listOf("com.example.graphql")
        queries = listOf(TaskQueries())
        mutations = listOf(TaskMutations())
    }
}
```

**Hlavní komponenty:**
- **Schema** - popisuje dostupné typy a operace
- **Query** - dotazy pro čtení dat
- **Mutation** - operace pro změnu dat
- **Resolver** - funkce pro získání dat

**Výhody:**
- Klient specifikuje přesně jaká data potřebuje (předchází over-fetching/under-fetching)
- Možnost získání více zdrojů v jediném požadavku
- Silné typování a introspekce
- Vývoj bez verzování API

## REST API

### Swagger a OpenAPI

OpenAPI (dříve Swagger) je specifikace pro dokumentaci REST API, která poskytuje standardizovaný formát pro popis endpointů, parametrů, requestů, responsů a dalších aspektů API. V našem projektu udržujeme OpenAPI specifikaci v YAML formátu (`documentation.yaml`), který slouží jako single source of truth pro naše API. Tato specifikace je využívána k automatickému generování interaktivní dokumentace pomocí Swagger UI, což výrazně zlepšuje developer experience - vývojáři mohou procházet dostupné endpointy, vidět příklady požadavků a odpovědí, a dokonce testovat API přímo z prohlížeče. OpenAPI specifikace také umožňuje automatické generování klientských knihoven pro různé programovací jazyky, což urychluje integraci s jinými systémy. V projektu využíváme pokročilé funkce specifikace jako jsou schémata pro validaci vstupů/výstupů, příklady dat pro testování a mockování, a security definice pro autentizaci. Pro zajištění aktuálnosti dokumentace máme nastaven proces, kdy změny v API musí být reflektovány v OpenAPI specifikaci.

```yaml
openapi: 3.0.1
info:
  title: "Task API"
  version: "1.0.0"
paths:
  /tasks:
    get:
      summary: "Get all tasks"
      responses:
        "200":
          description: "Successful response"
```

**Výhody:**
- Interaktivní dokumentace
- Automatické generování klientů
- Standardizovaný formát pro API popis
- Možnost mockování dat

## Dependency Injection

### Koin

Koin je lightweight dependency injection framework pro Kotlin, který na rozdíl od konkurenčních řešení jako Dagger nebo Spring nevyužívá reflexi nebo generování kódu v době kompilace. Místo toho používá DSL pro definici závislostí, což vede k čitelnějšímu a přímočarému kódu. V našem projektu používáme Koin pro správu závislostí v celé aplikaci, což umožňuje snadné testování a modularizaci. Hlavními koncepty jsou Module (kontejner s definicemi závislostí), Factory (vytváří novou instanci při každém požadavku), Single (singleton instance) a Scope (instance vázané na určitý životní cyklus). Koin se snadno integruje s Ktorem pomocí extension funkcí a nevyžaduje žádné speciální anotace nebo build procesory. Framework také poskytuje pokročilé funkce jako lazy injection, property injection nebo kvalifikátory pro rozlišení implementací stejného rozhraní. Díky své jednoduchosti a Kotlin-first přístupu je Koin ideální volbou pro projekty, kde preferujete transparentní DI řešení s minimálním boilerplate kódem.

```kotlin
val appModule = module {
    single { DatabaseFactory() }
    single { TaskRepository(get()) }
    single<TaskService> { TaskServiceImpl(get()) }
}

fun Application.configureDependencyInjection() {
    startKoin {
        modules(appModule)
    }
}
```

**Hlavní koncepty:**
- **Module** - kontejner s definicemi závislostí
- **Factory** - vytváří novou instanci při každém požadavku
- **Single** - singleton instance
- **Scope** - instance vázané na určitý životní cyklus

### Anotace

Koin od verze 3.2 podporuje KSP (Kotlin Symbol Processing) pro definici závislostí pomocí anotací, což dále zjednodušuje práci s dependency injection. Tento přístup kombinuje výhody deklarativního DI s jednoduchostí Koinu. Místo ručního definování modulů a závislostí stačí označit třídy anotacemi jako `@Single`, `@Factory` nebo `@Scope`, a Koin automaticky vygeneruje potřebné definice. V našem projektu využíváme tento přístup zejména pro service a repository třídy, což výrazně redukuje boilerplate kód a zvyšuje čitelnost. Anotační přístup přináší několik výhod: je přímočařejší a intuitivnější pro vývojáře zvyklé na jiné DI frameworky, automaticky vyřeší závislosti na základě konstruktorů, a je plně kompatibilní s ostatními částmi Koinu včetně modulárního přístupu. Zároveň je tento přístup rychlejší než řešení založená na reflexi, protože KSP pracuje v době kompilace a generuje optimalizovaný kód.

```kotlin
@Single
class TaskServiceImpl(
    private val repository: TaskRepository
) : TaskService
```

**Výhody:**
- Jednodušší kód (méně boilerplate)
- Automatická registrace závislostí
- Kompatibilní s modulárním přístupem
- Rychlejší než reflexe

## Logování

### logback-classic

Logback-classic je robustní a flexibilní implementace SLF4J API pro logování v JVM aplikacích, která nabízí vynikající výkon, široké možnosti konfigurace a pokročilé funkce. V našem projektu je základním nástrojem pro zaznamenávání informací o běhu aplikace, chybách a událostech. Konfigurace probíhá přes XML soubor `logback.xml`, který definuje apendery (kam se logy zapisují), formáty logů a úrovně logování pro různé části aplikace. Využíváme kombinaci konzolového výstupu pro vývoj a souborového výstupu s rotací pro produkci. Logback poskytuje mnoho užitečných funkcí jako je automatická komprese starých log souborů, podmíněné logování podle prostředí, filtry na základě vzorů nebo integrace s monitorovacími systémy. Důležitou vlastností je také podpora MDC (Mapped Diagnostic Context), která umožňuje přidávat kontextové informace k logům, což je neocenitelné při trasování požadavků v distribuovaných systémech.

**Konfigurace v `src/main/resources/logback.xml`:**
```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    <root level="info">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

### CallLoging Plugin

CallLogging je Ktor plugin, který automaticky zaznamenává informace o HTTP požadavcích a odpovědích, což poskytuje cenný přehled o provozu aplikace a usnadňuje debugging. V našem projektu je nakonfigurován tak, aby logoval informace o metodě, URL, status kódu a době zpracování pro všechny API požadavky. Plugin lze flexibilně nastavit - můžete určit, které cesty mají být logovány, jaké detaily mají být zaznamenány nebo dokonce implementovat vlastní formátovací funkci. V produkčním prostředí je logování nastaveno selektivně, aby nezahlcovalo log soubory, zatímco v development módu poskytuje podrobnější informace. Při zpracování velkého množství požadavků je důležitá i efektivita pluginu - implementace minimalizuje overhead a používá non-blocking přístup, takže má minimální vliv na výkon aplikace. Plugin je také dobře integrován s MDC, takže každý log obsahuje identifikátor požadavku, což umožňuje snadné trasování celého lifecycle requestu.

```kotlin
install(CallLogging) {
    level = Level.INFO
    filter { call -> call.request.path().startsWith("/api") }
    format { call ->
        "${call.request.httpMethod.value} ${call.request.uri} -> ${call.response.status()}"
    }
}
```

**Účel:** Automatické logování informací o HTTP požadavcích a odpovědích.

### Asynchronní logování (AsyncAppender)

Asynchronní logování prostřednictvím AsyncAppender je technika, která výrazně minimalizuje dopad logování na výkon aplikace, zejména při intenzivním provozu nebo pomalém I/O. Místo blokování hlavního aplikačního vlákna při zápisu logů do souboru nebo databáze, AsyncAppender umisťuje log zprávy do in-memory bufferu, odkud jsou zpracovávány samostatným vláknem. V našem projektu používáme tuto techniku v produkčním prostředí, kde je minimální latence klíčová. V konfiguraci je definována velikost fronty, strategie pro případ přeplnění (např. zahazování méně důležitých zpráv) a počet worker vláken. Tímto přístupem dosahujeme až 10x vyšší propustnosti logování při špičkách zatížení. AsyncAppender je nicméně potřeba používat s rozmyslem - v případě náhlého ukončení aplikace mohou být některé zprávy ve frontě ztraceny, a pro kritické bezpečnostní logy může být vhodnější synchronní přístup.

```xml
<appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
    <queueSize>512</queueSize>
    <appender-ref ref="FILE"/>
</appender>
```

**Výhody:**
- Neblokuje hlavní vlákno aplikace
- Zvyšuje propustnost systému
- Buffer pro log zprávy

### Kotlin Logging

Kotlin Logging je wrapper knihovna nad SLF4J, která poskytuje Kotlin-friendly API pro logování s využitím výhod jazyka jako jsou extension functions, lambda výrazy a null safety. Hlavní inovací této knihovny je lazy evaluation log zpráv - výrazy v bloku loggeru jsou vyhodnoceny pouze pokud je daná úroveň logování aktivní. V našem projektu to využíváme zejména pro debug logy, které často obsahují výpočetně náročné operace (např. serializace objektů nebo formátování složitých struktur). Knihovna také elegantně řeší problém null-safety u logovaných objektů a nabízí přehlednější syntaxi díky extension funkcím. Pro vývojáře je příjemné, že kód je kratší a čitelnější než při použití standardního SLF4J API. V kombinaci s korutinami poskytuje knihovna i specifické metody pro logování v asynchronním kontextu, což je v Ktor aplikaci, kde většina kódu běží v korutinách, velmi užitečné.

```kotlin
private val logger = KotlinLogging.logger {}

fun someFunction() {
    logger.info { "This is logged with minimal overhead" }
    logger.debug { "Expensive operation result: ${calculateExpensiveValue()}" }
}
```

**Výhody:**
- Lazy evaluation log zpráv
- Kompatibilita s Kotlin korutinami
- Čistší a stručnější syntaxe
- Eliminace null-check operací

## Databáze

### Migrace - Flyway

Flyway je nástroj pro správu verzí databázového schématu, který umožňuje sledovat, plánovat a aplikovat změny databázové struktury konzistentním a opakovatelným způsobem. V našem projektu je Flyway nakonfigurován tak, aby se automaticky spustil při startu aplikace, zkontroloval aktuální stav databáze a aplikoval všechny chybějící migrace. Migrační skripty jsou pojmenovány podle konvence (např. `V1__Create_tasks_table.sql`) a uloženy v `src/main/resources/db/migration`. Každá migrace je provedena v rámci transakce a Flyway udržuje speciální tabulku s historií provedených migrací, což zabraňuje dvojí aplikaci stejných změn. Tento přístup přináší několik klíčových výhod: verzované databázové schéma, které lze snadno rollbackovat, automatické aplikování změn při nasazení, podpora různých prostředí (dev, test, prod) s různými stavy a validace integrity migrací. Flyway podporuje nejen SQL skripty, ale i Java/Kotlin kód pro komplexnější migrační scénáře.

```kotlin
fun configureFlyway(dataSource: DataSource) {
    val flyway = Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .load()
    flyway.migrate()
}
```

**Struktura migrací:**
- Soubory pojmenované `V1__Create_tasks_table.sql`, `V2__Add_status_column.sql`...
- Uložené v `src/main/resources/db/migration`

**Výhody:**
- Verzované databázové schéma
- Automatické aplikování změn při startu
- Podpora různých prostředí (dev, test, prod)
- Validace integrity migrací

### HikariCP

HikariCP je high-performance JDBC connection pool, který je považován za jeden z nejrychlejších a nejspolehlivějších connection poolů pro JVM aplikace. V našem projektu slouží jako efektivní prostředník mezi aplikací a PostgreSQL databází, spravující a recyklující databázová spojení pro optimální výkon. HikariCP je navržen s důrazem na rychlost a minimální overhead - využívá bytecode-level optimalizace, minimalizuje alokace objektů a implementuje sofistikované metody pro detekci a obnovu problematických spojení. V konfiguraci nastavujeme parametry jako maximální velikost poolu, timeout pro získání spojení nebo max lifetime spojení. Pro produkční nasazení jsou důležité i pokročilé funkce jako health check, metriky výkonu nebo leak detection. Oproti starším alternativám (Apache DBCP, C3P0) nabízí HikariCP výrazně lepší výkon a stabilitu i při vysoké zátěži. Další výhodou je integrace s JVM metrikami a monitorovacími nástroji, což umožňuje detailní sledování výkonu databázové vrstvy.

```kotlin
val config = HikariConfig().apply {
    jdbcUrl = "jdbc:postgresql://localhost:5432/mydatabase"
    driverClassName = "org.postgresql.Driver"
    username = "user"
    password = "password"
    maximumPoolSize = 10
}
val dataSource = HikariDataSource(config)
```

**Klíčové vlastnosti:**
- Optimalizovaný pro rychlost a stabilitu
- Monitorování a metriky
- Nastavitelné parametry (pool size, timeout...)
- Zdravé výchozí hodnoty

## JWT autentizace s Ktor auth pluginem: Kompletní přehled

### Co je JWT (JSON Web Token)

JWT je otevřený standard (RFC 7519) pro bezpečný přenos informací mezi stranami jako JSON objekt. Tyto informace mohou být ověřeny a důvěryhodné, protože jsou digitálně podepsané. JWTs mohou být podepsány pomocí tajného klíče (s algoritmem HMAC) nebo pomocí veřejného/soukromého klíčového páru (RSA nebo ECDSA).

#### Struktura JWT tokenu

JWT token se skládá ze tří částí oddělených tečkami:

  ```
  header.payload.signature
  ```

1. **Header** - typicky obsahuje typ tokenu (JWT) a použitý algoritmus podpisu (např. HMAC SHA256)
2. **Payload** - obsahuje tvrzení ("claims"), což jsou informace o uživateli a metadata
3. **Signature** - zajišťuje integritu tokenu a ověřuje, že odesílatel je skutečně ten, za koho se vydává

Každá část je Base64URL-encoded, což vytváří URL-bezpečný řetězec, který lze snadno přenášet v HTTP hlavičce.

### Jak funguje JWT autentizace v Ktor

Ktor poskytuje plugin `ktor-auth-jwt`, který usnadňuje implementaci JWT autentizace:

1. **Server vydá JWT token** při úspěšném přihlášení uživatele
2. **Klient ukládá token** (typicky v localStorage, sessionStorage nebo bezpečném úložišti)
3. **Klient zasílá token** s každým požadavkem (obvykle v Authorization hlavičce)
4. **Server ověřuje token** při každém požadavku na chráněné zdroje
5. **Server dekóduje token** pro získání informací o uživateli

### Typický flow autentizace s JWT

1. **Registrace uživatele**:
  - Uživatel poskytne přihlašovací údaje (email, heslo, atd.)
  - Server uloží hash hesla a další údaje o uživateli do databáze
  - Server může ihned vydat JWT token nebo požádat o přihlášení

2. **Přihlášení uživatele**:
  - Uživatel zadá přihlašovací údaje
  - Server ověří údaje proti databázi
  - Při úspěchu server vygeneruje JWT token obsahující identifikátor uživatele, role, a další relevantní data
  - Server pošle token zpět klientovi

3. **Autentizované požadavky**:
  - Klient připojí token k požadavku v hlavičce Authorization: Bearer {token}
  - Server ověří podpis tokenu a zkontroluje jeho platnost
  - Server zpracuje požadavek, pokud je token platný

4. **Obnovení tokenu**:
  - JWT tokeny mají typicky krátkou životnost (15-60 minut)
  - Pro dlouhodobé přihlášení se používá refresh token mechanismus
  - Klient získá nový JWT token pomocí refresh tokenu bez nutnosti opětovného zadání hesla

### Co budete potřebovat pro implementaci

#### Na straně serveru:

1. **JWT knihovna**: Ktor poskytuje `ktor-auth-jwt` plugin, který integruje knihovnu `java-jwt`
2. **Bezpečné úložiště pro tajný klíč**: Pro podpis/ověření JWT tokenů
3. **Uživatelský model a repository**: Pro správu uživatelů a jejich rolí
4. **Autentizační service**: Logika pro validaci přihlašovacích údajů a generování tokenů
5. **Autorizační mechanismus**: Pro kontrolu oprávnění uživatele k určitým operacím

#### Na straně klienta:

1. **Mechanismus pro ukládání JWT**:
  - Pro web: localStorage/sessionStorage nebo HttpOnly cookies
  - Pro mobilní aplikace: Secure storage (Keychain na iOS, KeyStore na Androidu)
2. **Interceptor/Middleware**: Pro automatické připojení JWT tokenu ke všem požadavkům
3. **Mechanismus pro refresh tokenů**: Detekce vypršení tokenu a jeho obnovení
4. **UI pro přihlášení/registraci**: Formuláře pro získání přihlašovacích údajů

### Bezpečnostní aspekty JWT

1. **Tajný klíč**: Musí být dostatečně dlouhý a bezpečně uložený
2. **Expirace tokenů**: Krátká doba platnosti (15-60 minut) minimalizuje škody při zcizení tokenu
3. **Obsah tokenu**: Neukládejte citlivé informace, pouze identifikátory a základní metadata
4. **Algoritmy podpisu**: Preferujte silné algoritmy (RS256, ES256) před jednodušším HS256
5. **Token invalidace**: JWT nemá zabudovaný mechanismus pro okamžité zneplatnění, řešení:
  - Krátká expirace + refresh token
  - Blacklist pro kompromitované tokeny
  - Redis nebo jiný in-memory store pro blacklist tokenů

### Scénáře a principy JWT autentizace

#### 1. Přihlášení uživatele

- Uživatel poskytne credentials
- Server ověří credentials proti databázi
- Server vygeneruje dva tokeny:
  - **Access token**: Krátkodobý JWT (15-60 minut) pro autentizaci požadavků
  - **Refresh token**: Dlouhodobý token (dny až týdny) pro získání nového access tokenu

#### 2. Přístup k chráněným zdrojům

- Klient zahrnuje JWT v hlavičce požadavku
- Server validuje token a extrahuje uživatelská data
- Server autorizuje přístup na základě rolí/oprávnění v tokenu

#### 3. Obnovení tokenu

- Access token vyprší
- Klient pošle refresh token na speciální endpoint
- Server ověří refresh token a vydá nový access token

#### 4. Odhlášení

- Clientside: Odstranění JWT z lokálního úložiště
- Serverside (volitelné): Přidání refresh tokenu na blacklist

### Výhody JWT v Ktoru

1. **Stateless autentizace**: Server nemusí uchovávat session data
2. **Škálovatelnost**: Bez centrálního session store, každý server může ověřit token nezávisle
3. **Dobrá integrace s Ktorem**: Jednoduchá konfigurace a použití
4. **Cross-domain podpora**: Funguje bez problémů mezi různými doménami
5. **Mobilní podpora**: Dobrá integrace s nativními mobilními aplikacemi

### Nevýhody a omezení

1. **Velikost tokenu**: JWT tokeny jsou větší než tradiční session ID
2. **Problém s okamžitou invalidací**: Nelze snadno zneplatnit před vypršením
3. **Bezpečnost ukládání**: Klient musí bezpečně ukládat token
4. **Riziko XSS**: Zejména při ukládání v localStorage na webu
5. **Komplexita**: Robustní implementace s refresh tokeny je složitější než tradiční sessions

### Co budete potřebovat specificky v Ktoru

1. **Závislosti**:
   ```
   implementation("io.ktor:ktor-server-auth:$ktor_version")
   implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
   implementation("com.auth0:java-jwt:3.18.2")
   ```

2. **Konfigurace**:
  - Nastavení JWT ověřovatele s tajným klíčem
  - Konfigurace validace claims
  - Nastavení realm a challenge

3. **API Endpointy**:
  - /register - pro vytvoření nového uživatele
  - /login - pro autentizaci a získání tokenu
  - /refresh - pro obnovení access tokenu
  - /logout - pro blacklisting aktivního refresh tokenu (volitelné)

### Shrnutí kroků implementace (vysokoúrovňový pohled)

1. Vytvoření modelu uživatele a databázového schématu
2. Implementace registračního a přihlašovacího endpointu
3. Konfigurace JWT autentizace v Ktoru
4. Implementace secured routes s využitím JWT
5. Implementace refresh token mechanismu
6. Implementace klientské logiky pro ukládání a použití tokenů
7. Aplikace bezpečnostních best practices

JWT autentizace v Ktoru nabízí moderní, bezpečný a škálovatelný způsob autentizace uživatelů, který dobře vyhovuje RESTful API a je vhodný jak pro webové, tak mobilní klienty. I když vyžaduje více nastavení než tradiční sessions, poskytuje větší flexibilitu a lepší uživatelský zážitek, zejména v distribuovaných systémech.

## AWS S3 a Pre-signed URL

### Amazon S3 - Simple Storage Service

Amazon S3 je objektové úložiště navržené pro ukládání a retrievování libovolného množství dat odkudkoli na internetu. Jedná se o vysoce škálovatelnou, spolehlivou a nízkonákladovou infrastrukturu, kterou využívají tisíce aplikací po celém světě. S3 je postaven na tzv. "pay-as-you-go" modelu, kde platíte pouze za úložiště a data transfer, které skutečně využijete.

**Klíčové koncepty AWS S3:**
- **Bucket** - základní kontejner pro ukládání objektů. Název bucketu musí být globálně unikátní v rámci celého AWS
- **Object** - základní entita ukládaná v S3 (soubor a jeho metadata)
- **Key** - unikátní identifikátor objektu v rámci bucketu (de facto cesta k souboru)
- **Region** - geografická lokace, kde AWS provozuje vaše S3 buckety
- **Endpoint** - URL přístupový bod pro AWS API

S3 poskytuje REST API, které umožňuje vytvářet, číst, aktualizovat a mazat objekty pomocí standardních HTTP metod (GET, PUT, POST, DELETE). Každý objekt je dostupný přes URL ve formátu: `https://<bucket-name>.s3.<region>.amazonaws.com/<object-key>`.

### Pre-signed URL - Princip fungování

Pre-signed URL je URL s omezenou časovou platností, které obsahuje zakódované autorizační informace umožňující provést specifickou operaci nad objektem v úložišti bez nutnosti mít přímé přihlašovací údaje k danému úložišti. Představuje bezpečný způsob, jak delegovat přímý přístup k objektům v cloudovém úložišti.

**Princip fungování pre-signed URL:**
1. Server s oprávněním přístupu k úložišti vygeneruje speciální URL
2. URL obsahuje šifrovaný podpis vytvořený z přístupových klíčů serveru
3. URL má stanovenu omezenou dobu platnosti (expiraci)
4. URL může být omezeno na konkrétní HTTP metodu (GET, PUT)
5. Klient může použít toto URL přímo bez dalších ověřovacích údajů

### Pre-signed URL v AWS S3

V kontextu AWS S3 pre-signed URL umožňuje generovat dočasný přístupový odkaz, který dovoluje uživatelům bez AWS přihlašovacích údajů provádět specifické operace nad S3 objekty - typicky upload (PUT) nebo download (GET) souborů. Toto řešení elegantně obchází běžná omezení přímého přístupu ke cloudovému úložišti.

**Flow při práci s pre-signed URL v AWS S3:**

1. **Generování URL na serveru:**
  - Aplikační server, který má AWS přístupové klíče, vytvoří pre-signed URL
  - Při generování specifikuje operaci (GET/PUT), bucket, klíč objektu a dobu platnosti
  - URL obsahuje všechny potřebné autorizační parametry a podpis

2. **Předání URL klientovi:**
  - Server poskytne vygenerované URL klientské aplikaci
  - Typicky jako odpověď na API požadavek klienta

3. **Použití URL klientem:**
  - Klient provede HTTP požadavek přímo na pre-signed URL
  - Pro download (GET): klient získá objekt přímo z S3
  - Pro upload (PUT): klient nahraje data přímo do S3

4. **Zpracování na straně AWS S3:**
  - S3 ověří platnost URL a jeho podpisu
  - Zkontroluje, zda URL nevypršelo
  - Pokud je vše v pořádku, povolí požadovanou operaci

### Typické scénáře použití pre-signed URL

1. **Upload souborů přímo do S3:**
  - Uživatel potřebuje nahrát soubor (např. profilový obrázek)
  - Backend generuje pre-signed PUT URL
  - Frontend nahrává soubor přímo do S3 bez průchodu přes server
  - Po úspěšném nahrání server uloží metadata do databáze

2. **Bezpečné stahování privátních souborů:**
  - Soubory v S3 jsou uloženy jako privátní (nepřístupné veřejnosti)
  - Při požadavku na stažení server generuje dočasné pre-signed GET URL
  - Uživatel může soubor stáhnout přímo z S3, ale jen po omezenou dobu

3. **Streamování médií bez veřejného přístupu:**
  - Video/audio soubory jsou uloženy privátně
  - Pro každé přehrání se generuje krátkodobé pre-signed URL
  - Zajišťuje kontrolu přístupu k prémiového obsahu

### Výhody použití pre-signed URL s AWS S3

1. **Efektivita a škálovatelnost:**
  - Data tečou přímo mezi klientem a S3, bez zátěže aplikačního serveru
  - S3 je vysoce škálovatelné pro paralelní uploady/downloady

2. **Bezpečnost:**
  - Aplikační server nikdy neexponuje AWS přístupové klíče klientům
  - Možnost nastavit krátkou expiraci URL (minuty)
  - Omezení na konkrétní operaci a soubor

3. **Spolehlivost:**
  - Využívá robustní AWS infrastrukturu
  - Automatická redundance dat v rámci S3
  - Dostupnost 99.99% garantovaná AWS

4. **Kontrola přístupu:**
  - Granulární kontrola - každé URL je specifické pro jeden objekt a operaci
  - Časově omezený přístup - URL automaticky expirují

### Praktické aspekty implementace

**Bezpečnostní best practices:**
- Nastavujte co nejkratší možnou expiraci URL (minuty, ne dny)
- Validujte typ a velikost souborů před generováním upload URL
- Implementujte dodatečnou autorizaci před generováním URL
- Používejte HTTPS pro všechny komunikace

**Technické aspekty:**
- Správná konfigurace CORS v S3 pro cross-origin požadavky
- Implementace Content-Type restrictions pro uploady
- Nastavení maximální velikosti souborů
- Automatické ověření úspěšnosti uploadu

Pre-signed URL v kombinaci s AWS S3 představuje elegantní, bezpečné a vysoce škálovatelné řešení pro správu uživatelských souborů v moderních cloudových aplikacích, které minimalizuje zátěž aplikačních serverů a poskytuje výborný uživatelský zážitek.


---

Tento průvodce pokrývá hlavní technologie a koncepty použité v projektu. Pro detailnější informace k jednotlivým technologiím navštivte jejich oficiální dokumentaci.