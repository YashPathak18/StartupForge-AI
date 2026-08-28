import json
from typing import TypeVar

from openai import AsyncOpenAI
from pydantic import BaseModel

from app.config import settings

Model = TypeVar("Model", bound=BaseModel)


class LlmService:
    async def structured(self, instructions: str, input_text: str, schema: type[Model]) -> Model | None:
        config = settings()
        if not config.openai_api_key:
            return None
        client = AsyncOpenAI(api_key=config.openai_api_key)
        response = await client.responses.create(
            model=config.openai_model,
            instructions=instructions,
            input=input_text,
            store=False,
            text={
                "format": {
                    "type": "json_schema",
                    "name": schema.__name__.lower(),
                    "strict": True,
                    "schema": schema.model_json_schema(),
                }
            },
        )
        return schema.model_validate(json.loads(response.output_text))
