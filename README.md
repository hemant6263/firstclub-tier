Steps to run :
Clone repo
```bash
cd ./firstclub-tier
mvn spring-boot:run
```


Run UTs:
```bash
mvn test
mvn test -Dtest=JsonRuleEvaluatorTest        # recursive rule evaluation
mvn test -Dtest=SubscriptionStateMachineTest  # all valid/invalid transitions
mvn test -Dtest=SubscriptionServiceTest       # subscribe, duplicate, direction guard, cancel
```

Low Level Arch: 

### Patterns Used:
1. Stratergy
   Each tier qualification rule is a strategy. JsonRuleEvaluator dispatches to the right evaluation logic based on rule type.
   RuleEvaluator (interface)
      JsonRuleEvaluator
              evaluates ConditionRule  → field + operator + value
               evaluates CompositeRule → AND/OR of child rules
2. State
  enforces valid lifecycle transitions. Invalid ones throw 409.

  ACTIVE → CANCELLED / EXPIRED / UPGRADED / DOWNGRADED
  Any other transition → IllegalStateException
3. Decorator Pattern
  Wraps every response with cross-cutting metadata without controllers knowing about it.


Concurrency is handled at DB level using @version (Optimistic locking)

Extra: Audit trail added

APIs:
### Membership Plans & Tiers
```bash
   curl http://localhost:8080/api/v1/memberships/plans
   curl http://localhost:8080/api/v1/memberships/tiers
```

### Subscriptions
```bash
# Subscribe
curl -X POST http://localhost:8080/api/v1/memberships/subscribe \
  -d '{"userId":1, "planType":"YEARLY", "tierId":3}'

# Current membership
curl http://localhost:8080/api/v1/memberships/current?userId=1

# Paginated history
curl "http://localhost:8080/api/v1/memberships/history?userId=1&page=0&size=10"

# Upgrade (tierId must be higher rank than current)
curl -X PUT http://localhost:8080/api/v1/memberships/1/upgrade \
  -d '{"tierId":3}'

# Downgrade (tierId must be lower rank than current)
curl -X PUT http://localhost:8080/api/v1/memberships/1/downgrade \
  -d '{"tierId":1}'

# Cancel
curl -X PUT http://localhost:8080/api/v1/memberships/1/cancel
```

### Admin (Dynamic Tier Management)
```bash
bash
# Create DIAMOND tier
curl -X POST http://localhost:8080/api/v1/admin/tiers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "DIAMOND", "rank": 4,
    "qualificationRule": {
      "type": "COMPOSITE", "operator": "AND", "rules": [
        {"type":"CONDITION","field":"monthlyOrderCount","operator":"GTE","value":50},
        {"type":"CONDITION","field":"monthlyOrderValue","operator":"GTE","value":100000}
      ]
    },
    "benefits": [
      {"type":"DISCOUNT_PERCENTAGE","value":"30"},
      {"type":"FREE_DELIVERY","value":null},
      {"type":"CASHBACK","value":"5"}
    ]
  }'

# Update GOLD criteria
curl -X PUT http://localhost:8080/api/v1/admin/tiers/2 \
  -H "Content-Type: application/json" \
  -d '{"name":"GOLD","rank":2,
       "qualificationRule":{"type":"CONDITION","field":"monthlyOrderCount","operator":"GTE","value":10},
       "benefits":[{"type":"DISCOUNT_PERCENTAGE","value":"15"},{"type":"FREE_DELIVERY","value":null}]}'
```

Extra: 
### Order Service Integration
```bash
  bash
  curl http://localhost:8080/api/v1/memberships/benefits/1
```


### Initialisation 
DataInitializer :
seeds SILVER/GOLD/PLATINUM with JSON rules built using helper methods
Users — 3 users pre seeded to demo all tier levels:
- Alice: VIP, 25 orders, ₹25k  qualifies PLATINUM
- Bob: REGULAR, 8 orders, ₹6k  qualifies GOLD
- Charlie: REGULAR, 2 orders, ₹500  SILVER only

ddl-auto=create-drop is just to demo we can set to none on production (replacing with liquibase) and demo purpose i have used sql we can add more profiles and data bases same we can add @ConditionalOnProperty on DataInitializer on actial ENVs
